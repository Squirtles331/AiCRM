package com.aicrm.module.identity.service.impl;

import com.aicrm.common.ResultCode;
import com.aicrm.common.exception.BusinessException;
import com.aicrm.module.conversation.entity.Conversation;
import com.aicrm.module.conversation.mapper.ConversationMapper;
import com.aicrm.module.identity.entity.Identity;
import com.aicrm.module.identity.entity.IdentityMapping;
import com.aicrm.module.identity.mapper.IdentityMapper;
import com.aicrm.module.identity.mapper.IdentityMappingMapper;
import com.aicrm.module.identity.service.IdentityService;
import com.aicrm.module.lead.entity.Lead;
import com.aicrm.module.lead.mapper.LeadMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * 渠道身份归一服务实现（3.2.2）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IdentityServiceImpl extends ServiceImpl<IdentityMapper, Identity> implements IdentityService {

    private static final String ENTITY_LEAD = "lead";

    private final IdentityMappingMapper identityMappingMapper;
    private final LeadMapper leadMapper;
    private final ConversationMapper conversationMapper;

    @Override
    public Identity ensureIdentity(Long tenantId, String identityType, String identityValue) {
        if (!StringUtils.hasText(identityType) || !StringUtils.hasText(identityValue)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "身份类型与值不能为空");
        }
        Identity identity = baseMapper.selectOne(new LambdaQueryWrapper<Identity>()
                .eq(Identity::getTenantId, tenantId)
                .eq(Identity::getIdentityType, identityType)
                .eq(Identity::getIdentityValue, identityValue)
                .last("LIMIT 1"));
        if (identity != null) {
            return identity;
        }
        identity = new Identity();
        identity.setTenantId(tenantId);
        identity.setIdentityType(identityType);
        identity.setIdentityValue(identityValue);
        identity.setStatus(1);
        identity.setConsent(0);
        try {
            baseMapper.insert(identity);
            return identity;
        } catch (DuplicateKeyException e) {
            // 并发注册：唯一约束兜底，重查返回
            return baseMapper.selectOne(new LambdaQueryWrapper<Identity>()
                    .eq(Identity::getTenantId, tenantId)
                    .eq(Identity::getIdentityType, identityType)
                    .eq(Identity::getIdentityValue, identityValue)
                    .last("LIMIT 1"));
        }
    }

    @Override
    public Long resolveLeadId(Long tenantId, String identityType, String identityValue) {
        if (!StringUtils.hasText(identityType) || !StringUtils.hasText(identityValue)) {
            return null;
        }
        Identity identity = baseMapper.selectOne(new LambdaQueryWrapper<Identity>()
                .eq(Identity::getTenantId, tenantId)
                .eq(Identity::getIdentityType, identityType)
                .eq(Identity::getIdentityValue, identityValue)
                .last("LIMIT 1"));
        if (identity == null) {
            return null;
        }
        IdentityMapping mapping = identityMappingMapper.selectOne(new LambdaQueryWrapper<IdentityMapping>()
                .eq(IdentityMapping::getTenantId, tenantId)
                .eq(IdentityMapping::getIdentityId, identity.getId())
                .eq(IdentityMapping::getEntityType, ENTITY_LEAD)
                .orderByDesc(IdentityMapping::getId)
                .last("LIMIT 1"));
        return mapping == null ? null : mapping.getEntityId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindToLead(Long tenantId, String identityType, String identityValue, Long leadId, String source) {
        if (leadId == null || leadMapper.selectById(leadId) == null) {
            throw new BusinessException(ResultCode.LEAD_NOT_FOUND, "线索不存在");
        }
        Identity identity = ensureIdentity(tenantId, identityType, identityValue);
        // 幂等：已绑定该线索则跳过；已绑定其他线索视为冲突，忽略（由 resolveLeadId 前置规避）
        IdentityMapping exists = identityMappingMapper.selectOne(new LambdaQueryWrapper<IdentityMapping>()
                .eq(IdentityMapping::getTenantId, tenantId)
                .eq(IdentityMapping::getIdentityId, identity.getId())
                .eq(IdentityMapping::getEntityType, ENTITY_LEAD)
                .last("LIMIT 1"));
        if (exists != null) {
            if (exists.getEntityId().equals(leadId)) {
                return;
            }
            log.warn("身份已绑定其他线索，忽略绑定 identityId={}, boundLeadId={}, targetLeadId={}",
                    identity.getId(), exists.getEntityId(), leadId);
            return;
        }
        IdentityMapping mapping = new IdentityMapping();
        mapping.setTenantId(tenantId);
        mapping.setIdentityId(identity.getId());
        mapping.setEntityType(ENTITY_LEAD);
        mapping.setEntityId(leadId);
        mapping.setConfidence(BigDecimal.ONE);
        mapping.setSource(source);
        identityMappingMapper.insert(mapping);
    }

    @Override
    public List<IdentityMapping> listMappingsByEntity(String entityType, Long entityId) {
        if (!StringUtils.hasText(entityType) || entityId == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "实体类型与实体 ID 不能为空");
        }
        return identityMappingMapper.selectList(new LambdaQueryWrapper<IdentityMapping>()
                .eq(IdentityMapping::getEntityType, entityType)
                .eq(IdentityMapping::getEntityId, entityId)
                .orderByDesc(IdentityMapping::getId));
    }

    @Override
    public void removeMapping(Long mappingId) {
        if (identityMappingMapper.selectById(mappingId) == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "身份映射不存在");
        }
        identityMappingMapper.deleteById(mappingId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long mergeLeads(Long primaryLeadId, Long secondaryLeadId) {
        if (primaryLeadId.equals(secondaryLeadId)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "不能合并自身");
        }
        Lead primary = requireLead(primaryLeadId);
        requireLead(secondaryLeadId);
        Long tenantId = primary.getTenantId();

        // 1. 次要线索的身份映射改绑到主线索
        identityMappingMapper.update(null, new LambdaUpdateWrapper<IdentityMapping>()
                .eq(IdentityMapping::getTenantId, tenantId)
                .eq(IdentityMapping::getEntityType, ENTITY_LEAD)
                .eq(IdentityMapping::getEntityId, secondaryLeadId)
                .set(IdentityMapping::getEntityId, primaryLeadId));

        // 2. 次要线索的会话改挂主线索
        conversationMapper.update(null, new LambdaUpdateWrapper<Conversation>()
                .eq(Conversation::getTenantId, tenantId)
                .eq(Conversation::getLeadId, secondaryLeadId)
                .set(Conversation::getLeadId, primaryLeadId));

        // 3. 逻辑删除次要线索
        leadMapper.deleteById(secondaryLeadId);

        log.info("线索合并完成 primaryLeadId={}, secondaryLeadId={}", primaryLeadId, secondaryLeadId);
        return primaryLeadId;
    }

    private Lead requireLead(Long id) {
        Lead lead = leadMapper.selectById(id);
        if (lead == null) {
            throw new BusinessException(ResultCode.LEAD_NOT_FOUND, "线索不存在");
        }
        return lead;
    }
}
