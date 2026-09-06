package com.aicrm.module.wecom.service.impl;

import com.aicrm.common.PageResult;
import com.aicrm.module.conversation.entity.Conversation;
import com.aicrm.module.conversation.entity.Message;
import com.aicrm.module.conversation.mapper.ConversationMapper;
import com.aicrm.module.conversation.mapper.MessageMapper;
import com.aicrm.module.customer.entity.Customer;
import com.aicrm.module.customer.mapper.CustomerMapper;
import com.aicrm.module.identity.entity.Identity;
import com.aicrm.module.identity.entity.IdentityMapping;
import com.aicrm.module.identity.mapper.IdentityMapper;
import com.aicrm.module.identity.mapper.IdentityMappingMapper;
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

import java.util.ArrayList;
import java.util.List;

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
    private final ConversationMapper conversationMapper;
    private final MessageMapper messageMapper;
    private final ProductService productService;

    @Override
    public CustomerProfile profile(String externalUserId) {
        Lead lead = resolveLead(externalUserId);
        if (lead == null) {
            return new CustomerProfile(null, null, null, null, null, null, null, null, List.of());
        }
        Customer customer = lead.getCustomerId() == null ? null : customerMapper.selectById(lead.getCustomerId());
        List<String> tags = listTags(lead.getCustomerId());
        return new CustomerProfile(
                lead.getId(), lead.getStatus(), lead.getIntent(), lead.getScore(),
                customer == null ? null : customer.getId(),
                customer == null ? null : customer.getName(),
                customer == null ? null : customer.getIndustry(),
                customer == null ? null : customer.getRegion(),
                tags);
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
        String resolvedIntent = intent;
        if (!StringUtils.hasText(resolvedIntent) && lead != null) {
            resolvedIntent = lead.getIntent();
        }
        List<String> replies = List.of(
                suggestReply(resolvedIntent),
                "如需进一步协助，请告诉我具体需求，我会继续为您处理。");
        return new ReplySuggestion(resolvedIntent, replies);
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

    private String suggestReply(String intent) {
        if (!StringUtils.hasText(intent)) {
            return "您好，我是您的人工顾问，请告诉我您希望了解的产品或服务。";
        }
        return switch (intent) {
            case "quote" -> "您好，我是您的人工顾问。为准确报价，请提供应用场景、采购数量、预算范围和期望交期。";
            case "sample" -> "您好，已了解您的样品需求，请提供收货地址、联系人和联系电话。";
            case "selection" -> "您好，已了解您的选型需求，请补充应用场景与工况要求，我来为您推荐合适型号。";
            default -> "您好，我是您的人工顾问，请描述您的具体需求，我会为您处理。";
        };
    }
}
