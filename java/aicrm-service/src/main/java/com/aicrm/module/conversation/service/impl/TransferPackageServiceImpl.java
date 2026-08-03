package com.aicrm.module.conversation.service.impl;

import com.aicrm.common.ResultCode;
import com.aicrm.common.exception.BusinessException;
import com.aicrm.module.ai.client.AiServiceClient;
import com.aicrm.module.ai.dto.AiChatMessage;
import com.aicrm.module.ai.dto.AiSummaryRequest;
import com.aicrm.module.ai.dto.AiSummaryResponse;
import com.aicrm.module.conversation.dto.TransferPackage;
import com.aicrm.module.conversation.entity.Conversation;
import com.aicrm.module.conversation.entity.Message;
import com.aicrm.module.conversation.mapper.MessageMapper;
import com.aicrm.module.conversation.service.ConversationService;
import com.aicrm.module.conversation.service.TransferPackageService;
import com.aicrm.module.intent.entity.ExtractedField;
import com.aicrm.module.intent.entity.IntentAnalysis;
import com.aicrm.module.intent.mapper.ExtractedFieldMapper;
import com.aicrm.module.intent.mapper.IntentAnalysisMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 转人工交接包服务实现：AI 摘要 + 最近意向 + 缺失字段 + 推荐话术
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransferPackageServiceImpl implements TransferPackageService {

    /** 参与摘要与接手的最近消息数 */
    private static final int RECENT_MESSAGE_LIMIT = 20;

    /** 报价前应收集的标准字段 */
    private static final List<String> REQUIRED_FIELDS = List.of("scene", "qty", "budget", "lead_time", "model");

    private final ConversationService conversationService;
    private final MessageMapper messageMapper;
    private final IntentAnalysisMapper intentAnalysisMapper;
    private final ExtractedFieldMapper extractedFieldMapper;
    private final AiServiceClient aiServiceClient;
    private final com.aicrm.module.lead.mapper.LeadMapper leadMapper;
    private final com.aicrm.module.lead.service.LeadAssignService leadAssignService;

    @Override
    public TransferPackage build(Long conversationId, Long operatorId) {
        Conversation conversation = conversationService.getById(conversationId);
        if (conversation == null) {
            throw new BusinessException(ResultCode.CONVERSATION_NOT_FOUND);
        }
        // 3.3.4 销售分配：未指定接手坐席时，优先线索负责人，否则走分配引擎自动分配
        Long targetOperator = operatorId;
        if (targetOperator == null) {
            targetOperator = resolveOperator(conversation);
        }
        conversationService.transferToHuman(conversationId, targetOperator);
        TransferPackage pkg = assemble(conversation);
        pkg.setOperatorId(targetOperator);
        pkg.setTransferredAt(LocalDateTime.now());
        log.info("转人工交接包已生成 conversationId={}, operatorId={}, intent={}",
                conversationId, targetOperator, pkg.getIntent());
        return pkg;
    }

    /** 未指定坐席时解析接手人：线索负责人 → 分配引擎自动分配 → 无可用坐席抛错 */
    private Long resolveOperator(Conversation conversation) {
        if (conversation.getLeadId() != null) {
            com.aicrm.module.lead.entity.Lead lead = leadMapper.selectById(conversation.getLeadId());
            if (lead != null && lead.getOwnerId() != null) {
                return lead.getOwnerId();
            }
            if (lead != null) {
                leadAssignService.assignLead(lead.getId());
                lead = leadMapper.selectById(lead.getId());
                if (lead != null && lead.getOwnerId() != null) {
                    return lead.getOwnerId();
                }
            }
        }
        throw new BusinessException(ResultCode.BAD_REQUEST, "暂无可接手的在线坐席，请手动指派");
    }

    @Override
    public TransferPackage get(Long conversationId) {
        Conversation conversation = conversationService.getById(conversationId);
        if (conversation == null) {
            throw new BusinessException(ResultCode.CONVERSATION_NOT_FOUND);
        }
        return assemble(conversation);
    }

    private TransferPackage assemble(Conversation conversation) {
        TransferPackage pkg = new TransferPackage();
        pkg.setConversationId(conversation.getId());
        pkg.setTenantId(conversation.getTenantId());
        pkg.setLeadId(conversation.getLeadId());
        pkg.setConversationType(conversation.getConversationType());
        pkg.setOperatorId(conversation.getAssignedTo());

        // 1. 最近消息（按时间正序）
        List<Message> recent = messageMapper.selectList(new LambdaQueryWrapper<Message>()
                .eq(Message::getTenantId, conversation.getTenantId())
                .eq(Message::getConversationId, conversation.getId())
                .orderByDesc(Message::getId)
                .last("LIMIT " + RECENT_MESSAGE_LIMIT));
        Collections.reverse(recent);

        // 2. 对话摘要（AI 降级：不可用时给出提示，不阻塞转人工）
        AiSummaryResponse summaryResp = null;
        if (!recent.isEmpty()) {
            List<AiChatMessage> messages = recent.stream()
                    .map(m -> new AiChatMessage(m.getSenderType(), m.getContent()))
                    .toList();
            summaryResp = aiServiceClient.summary(
                    new AiSummaryRequest(conversation.getTenantId(), conversation.getId(), messages));
        }
        if (summaryResp != null && summaryResp.summary() != null) {
            pkg.setSummary(summaryResp.summary());
            pkg.setSummarySource("ai");
        } else {
            pkg.setSummary("AI 摘要服务暂不可用，请坐席查看最近 " + recent.size() + " 条消息记录。");
            pkg.setSummarySource("fallback");
        }

        // 3. 最近意向判定
        IntentAnalysis latestIntent = intentAnalysisMapper.selectOne(
                new LambdaQueryWrapper<IntentAnalysis>()
                        .eq(IntentAnalysis::getConversationId, conversation.getId())
                        .orderByDesc(IntentAnalysis::getId)
                        .last("LIMIT 1"));
        if (latestIntent != null) {
            pkg.setIntent(latestIntent.getIntent());
            pkg.setConfidence(latestIntent.getConfidence());
            pkg.setEvidence(latestIntent.getEvidence());
        }

        // 4. 缺失字段：标准字段减去已抽取字段
        pkg.setMissingFields(computeMissingFields(conversation.getTenantId(), conversation.getLeadId()));

        // 5. 推荐回复与下一步动作（按意向）
        applyIntentActions(pkg);

        return pkg;
    }

    private List<String> computeMissingFields(Long tenantId, Long leadId) {
        if (leadId == null) {
            return new ArrayList<>(REQUIRED_FIELDS);
        }
        List<ExtractedField> collected = extractedFieldMapper.selectList(
                new LambdaQueryWrapper<ExtractedField>()
                        .eq(ExtractedField::getTenantId, tenantId)
                        .eq(ExtractedField::getLeadId, leadId));
        Set<String> collectedKeys = new LinkedHashSet<>();
        for (ExtractedField f : collected) {
            if (f.getFieldKey() != null && f.getFieldValue() != null && !f.getFieldValue().isBlank()) {
                collectedKeys.add(f.getFieldKey());
            }
        }
        return REQUIRED_FIELDS.stream().filter(k -> !collectedKeys.contains(k)).toList();
    }

    private void applyIntentActions(TransferPackage pkg) {
        String intent = pkg.getIntent() == null ? "other" : pkg.getIntent();
        List<String> steps = new ArrayList<>();
        steps.add("AI 已生成对话摘要，坐席确认后回复客户");

        String reply;
        switch (intent) {
            case "quote" -> {
                reply = "您好，我是人工顾问，已为您整理报价需求。为准确报价，请补充：应用场景、采购数量、预算范围、期望交期，我尽快给您正式报价。";
                steps.add("补齐报价关键字段（" + String.join("/", pkg.getMissingFields()) + "）");
                steps.add("字段齐全后生成报价单，跟进报价意向");
            }
            case "sample" -> {
                reply = "您好，已为您登记样品申请。请提供收货地址、联系人及电话，我们尽快安排寄样。";
                steps.add("登记样品寄送单（收货信息）");
                steps.add("寄样后 3 日内跟进试用反馈");
            }
            case "selection" -> {
                reply = "您好，已收到您的选型需求。为给出精准推荐，请补充应用场景与工况要求，我们马上为您选型。";
                steps.add("输出选型对比表（推荐 2-3 款）");
                steps.add("选型确认后引导报价");
            }
            default -> {
                reply = "您好，我是人工顾问，已接手您的会话，请描述您的需求，我来为您处理。";
                steps.add("引导客户明确需求，必要时做意向判定");
            }
        }
        if (pkg.getMissingFields() != null && !pkg.getMissingFields().isEmpty()
                && ("quote".equals(intent) || "selection".equals(intent))) {
            steps.add("提示客户补充缺失信息（" + String.join("/", pkg.getMissingFields()) + "）");
        }
        steps.add("低置信度/复杂问题已转人工，坐席优先响应");

        pkg.setRecommendedReply(reply);
        pkg.setNextSteps(steps);
    }
}
