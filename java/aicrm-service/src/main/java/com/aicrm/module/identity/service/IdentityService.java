package com.aicrm.module.identity.service;

import com.aicrm.module.identity.entity.Identity;
import com.aicrm.module.identity.entity.IdentityMapping;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 渠道身份归一服务（3.2.2）
 * <p>
 * 核心：同一身份（手机号/邮箱/社媒ID/企微ID等）跨渠道关联到同一实体（lead/contact/customer），
 * 事件入池时按身份解析已有线索实现"自动识别复用"，避免同一客户重复建线索。
 */
public interface IdentityService extends IService<Identity> {

    /**
     * 幂等注册身份：按 (tenant, type, value) 唯一约束，存在则返回，不存在则创建
     */
    Identity ensureIdentity(Long tenantId, String identityType, String identityValue);

    /**
     * 通过身份解析已绑定的线索 ID（entity_type = lead），未绑定返回 null
     */
    Long resolveLeadId(Long tenantId, String identityType, String identityValue);

    /**
     * 注册身份并绑定到线索（幂等；若已绑定其他线索则忽略，不自动合并）
     */
    void bindToLead(Long tenantId, String identityType, String identityValue, Long leadId, String source);

    /**
     * 查询实体（lead/contact/customer）的身份映射列表（关系维护页面展示）
     */
    List<IdentityMapping> listMappingsByEntity(String entityType, Long entityId);

    /**
     * 删除身份映射
     */
    void removeMapping(Long mappingId);

    /**
     * 线索合并：次要线索的身份映射与会话归并到主线索，删除次要线索
     *
     * @return 合并后的主线索 ID
     */
    Long mergeLeads(Long primaryLeadId, Long secondaryLeadId);
}
