package com.aicrm.module.wecom.service.impl;

import com.aicrm.common.PageResult;
import com.aicrm.module.conversation.entity.Conversation;
import com.aicrm.module.conversation.entity.Message;
import com.aicrm.module.conversation.mapper.ConversationMapper;
import com.aicrm.module.conversation.mapper.MessageMapper;
import com.aicrm.module.conversation.service.AiChatService;
import com.aicrm.module.customer.entity.Customer;
import com.aicrm.module.customer.mapper.CustomerMapper;
import com.aicrm.module.identity.entity.Identity;
import com.aicrm.module.identity.entity.IdentityMapping;
import com.aicrm.module.identity.mapper.IdentityMapper;
import com.aicrm.module.identity.mapper.IdentityMappingMapper;
import com.aicrm.module.intent.entity.ExtractedField;
import com.aicrm.module.intent.entity.IntentAnalysis;
import com.aicrm.module.intent.mapper.ExtractedFieldMapper;
import com.aicrm.module.intent.mapper.IntentAnalysisMapper;
import com.aicrm.module.lead.entity.Lead;
import com.aicrm.module.lead.mapper.LeadMapper;
import com.aicrm.module.product.entity.Product;
import com.aicrm.module.product.service.ProductService;
import com.aicrm.module.tag.entity.CustomerTag;
import com.aicrm.module.tag.entity.CustomerTagRel;
import com.aicrm.module.tag.mapper.CustomerTagMapper;
import com.aicrm.module.tag.mapper.CustomerTagRelMapper;
import com.aicrm.module.wecom.service.WecomSidebarService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 企微侧边栏业务服务实现（3.3.5）
 * <p>身份归一规范：企微外部用户 ID 的身份为 wecom 类型，值 = "wecom:" + externalUserId（见 3.2.2）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WecomSidebarServiceImpl implements WecomSidebarService {

    private final IdentityMapper identityMapper;
    private final IdentityMappingMapper identityMappingMapper;
    private final LeadMapper leadMapper;
    private final CustomerMapper customerMapper;
    private final CustomerTagMapper tagMapper;
    private final CustomerTagRelMapper tagRelMapper;
    private final IntentAnalysisMapper intentAnalysisMapper;
    private final ConversationMapper conversationMapper;
    private final MessageMapper messageMapper;
    private final ExtractedFieldMapper extractedFieldMapper;
    private final ProductService productService;
    private final AiChatService aiChatService;

    @Override
    public CustomerProfile profile(String externalUserId) {
        Lead lead = resolveLead(externalUserId);
        if (lead == null) {
            return new CustomerProfile(null, null, null, null, null, null, null, null,
                    List.of(), null, null);
        }
        Customer customer = lead.getCustomerId() == null ? null : customerMapper.selectById(lead.getCustomerId());
        List<String> tags = listTags(lead.getCustomerId());
        IntentAnalysis latest = latestIntentByLead(lead.getId());
        return new CustomerProfile(
                lead.getId(), lead.getStatus(), lead.getIntent(), lead.getScore(),
                customer == null ? null : customer.getId(),
                customer == null ? null : customer.getName(),
                customer == null ? null : customer.getIndustry(),
                customer == null ? null : customer.getRegion(),
                tags,
                latest == null ? null : latest.getIntent(),
                latest == null ? null : latest.getConfidence());
    }

    @Override
    public List<ConversationBrief> history(String externalUserId) {
        Lead lead = resolveLead(externalUserId);
        if (lead == null) {
            return List.of();
        }
        List<Conversation> conversations = conversationMapper.selectList(new LambdaQueryWrapper<Conversation>()
                .eq(Conversation::getLeadId, lead.getId())
                .orderByDesc(Conversation::getLastMessageAt));
        List<ConversationBrief> result = new ArrayList<>();
        for (Conversation conversation : conversations) {
            Message last = messageMapper.selectOne(new LambdaQueryWrapper<Message>()
                    .eq(Message::getConversationId, conversation.getId())
                    .orderByDesc(Message::getId)
                    .last("LIMIT 1"));
            result.add(new ConversationBrief(
                    conversation.getId(), conversation.getConversationType(), conversation.getStatus(),
                    conversation.getLastMessageAt(),
                    last == null ? null : last.getContent(),
                    last == null ? null : last.getSenderType()));
        }
        return result;
    }

    @Override
    public ReplySuggestion replySuggestions(String externalUserId, String intent) {
        Lead lead = resolveLead(externalUserId);
        List<String> missingFields = computeMissingFields(lead);
        List<String> replies = new ArrayList<>();
        replies.add(aiChatService.suggestReply(intent));
        replies.add("如需人工介入请点击右上角转人工，我已将上下文同步给同事。");
        String resolvedIntent = intent;
        if (!StringUtils.hasText(resolvedIntent) && lead != null) {
            resolvedIntent = lead.getIntent();
        }
        return new ReplySuggestion(resolvedIntent, replies, missingFields);
    }

    @Override
    public PageResult<Product> products(String keyword, long page, long size) {
        // 侧边栏仅展示上架产品
        return productService.page(keyword, null, 1, page, size);
    }

    // ---------- 基础 ----------

    /** 按企微外部用户 ID 解析线索（identity → mapping → lead） */
    private Lead resolveLead(String externalUserId) {
        if (!StringUtils.hasText(externalUserId)) {
            return null;
        }
        Identity identity = identityMapper.selectOne(new LambdaQueryWrapper<Identity>()
                .eq(Identity::getIdentityType, "wecom")
                .eq(Identity::getIdentityValue, "wecom:" + externalUserId)
                .last("LIMIT 1"));
        if (identity == null) {
            // 兜底：部分历史数据按 sourceContentId 直接关联
            return leadMapper.selectOne(new LambdaQueryWrapper<Lead>()
                    .eq(Lead::getSourceContentId, externalUserId)
                    .last("LIMIT 1"));
        }
        IdentityMapping mapping = identityMappingMapper.selectOne(new LambdaQueryWrapper<IdentityMapping>()
                .eq(IdentityMapping::getIdentityId, identity.getId())
                .eq(IdentityMapping::getEntityType, "lead")
                .orderByDesc(IdentityMapping::getId)
                .last("LIMIT 1"));
        if (mapping == null) {
            return null;
        }
        return leadMapper.selectById(mapping.getEntityId());
    }

    private List<String> listTags(Long customerId) {
        if (customerId == null) {
            return List.of();
        }
        List<CustomerTagRel> rels = tagRelMapper.selectList(new LambdaQueryWrapper<CustomerTagRel>()
                .eq(CustomerTagRel::getCustomerId, customerId));
        if (rels.isEmpty()) {
            return List.of();
        }
        List<Long> tagIds = rels.stream().map(CustomerTagRel::getTagId).toList();
        return tagMapper.selectBatchIds(tagIds).stream().map(CustomerTag::getName).toList();
    }

    private IntentAnalysis latestIntentByLead(Long leadId) {
        Conversation conversation = conversationMapper.selectOne(new LambdaQueryWrapper<Conversation>()
                .eq(Conversation::getLeadId, leadId)
                .orderByDesc(Conversation::getId)
                .last("LIMIT 1"));
        if (conversation == null) {
            return null;
        }
        return intentAnalysisMapper.selectOne(new LambdaQueryWrapper<IntentAnalysis>()
                .eq(IntentAnalysis::getConversationId, conversation.getId())
                .orderByDesc(IntentAnalysis::getId)
                .last("LIMIT 1"));
    }

    /** 报价前标准字段与已抽取字段差集 */
    private List<String> computeMissingFields(Lead lead) {
        List<String> required = List.of("scene", "qty", "budget", "lead_time", "model");
        if (lead == null) {
            return new ArrayList<>(required);
        }
        List<ExtractedField> collected = extractedFieldMapper.selectList(new LambdaQueryWrapper<ExtractedField>()
                .eq(ExtractedField::getLeadId, lead.getId()));
        Set<String> keys = new LinkedHashSet<>();
        for (ExtractedField f : collected) {
            if (f.getFieldKey() != null && f.getFieldValue() != null && !f.getFieldValue().isBlank()) {
                keys.add(f.getFieldKey());
            }
        }
        return required.stream().filter(k -> !keys.contains(k)).toList();
    }
}
