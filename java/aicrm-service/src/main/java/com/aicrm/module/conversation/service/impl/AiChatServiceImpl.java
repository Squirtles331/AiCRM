package com.aicrm.module.conversation.service.impl;

import com.aicrm.module.ai.client.AiServiceClient;
import com.aicrm.module.ai.dto.AiAnswerRequest;
import com.aicrm.module.ai.dto.AiAnswerResponse;
import com.aicrm.module.ai.dto.AiChatMessage;
import com.aicrm.module.conversation.entity.Conversation;
import com.aicrm.module.conversation.entity.Message;
import com.aicrm.module.conversation.mapper.MessageMapper;
import com.aicrm.module.conversation.service.AiChatService;
import com.aicrm.module.conversation.service.ConversationService;
import com.aicrm.module.intent.entity.IntentAnalysis;
import com.aicrm.module.intent.service.IntentAnalysisService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * AI 对话内部服务实现（3.3.3）
 * <p>
 * 流程：客户消息落库 → 意图分析（AI，降级 null）→ 生成回复（RAG answer，降级规则话术）→ 回复落库。
 * 转人工建议：意向为 other / 置信度低于阈值 / AI 不可用。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatServiceImpl implements AiChatService {

    /** 参与回复生成的最近消息数 */
    private static final int CONTEXT_MESSAGE_LIMIT = 10;
    /** 低置信度阈值：低于该值建议转人工 */
    private static final double LOW_CONFIDENCE = 0.5;

    private final ConversationService conversationService;
    private final IntentAnalysisService intentAnalysisService;
    private final AiServiceClient aiServiceClient;
    private final MessageMapper messageMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiReply handleIncoming(Long conversationId, String customerMessage) {
        Conversation conversation = conversationService.detail(conversationId);
        if (!StringUtils.hasText(customerMessage)) {
            throw new IllegalArgumentException("客户消息不能为空");
        }

        // 1. 客户消息落库
        Message incoming = new Message();
        incoming.setTenantId(conversation.getTenantId());
        incoming.setConversationId(conversationId);
        incoming.setSenderType("customer");
        incoming.setMsgType("text");
        incoming.setContent(customerMessage);
        incoming.setAiGenerated(false);
        conversationService.appendMessage(incoming);

        // 2. 意向判定（AI 不可用时返回 null，不阻塞）
        IntentAnalysis intent = intentAnalysisService.analyzeByConversation(conversationId);

        // 3. 生成回复：AI 优先，降级规则话术
        String content = generateReply(conversation, customerMessage, intent);

        // 4. 回复落库
        Message reply = new Message();
        reply.setTenantId(conversation.getTenantId());
        reply.setConversationId(conversationId);
        reply.setSenderType("ai");
        reply.setMsgType("text");
        reply.setContent(content);
        reply.setAiGenerated(true);
        conversationService.appendMessage(reply);

        // 5. 转人工建议：低置信度 / 非业务意向 / AI 不可用
        boolean shouldTransfer = intent == null
                || "other".equals(intent.getIntent())
                || (intent.getConfidence() != null && intent.getConfidence().doubleValue() < LOW_CONFIDENCE);
        return new AiReply(content,
                intent == null ? null : intent.getIntent(),
                intent == null ? null : intent.getConfidence(),
                shouldTransfer);
    }

    @Override
    public String suggestReply(String intent) {
        return switch (intent == null ? "other" : intent) {
            case "quote" -> "您好，已收到您的咨询。为准确为您报价，请补充应用场景、采购数量、预算范围和期望交期，我尽快为您出具正式报价。";
            case "sample" -> "您好，可以为您安排样品。请提供收货地址和联系方式，我们核实后尽快寄样，并同步告知物流信息。";
            case "selection" -> "您好，为您选型需要了解应用场景与工况要求，方便的话请您补充，我们会推荐 2-3 款适配型号供对比。";
            default -> "收到您的消息啦，请问需要了解产品资料、获取报价还是申请样品呢？";
        };
    }

    @Override
    public TransferEvaluation evaluate(Long conversationId) {
        conversationService.detail(conversationId);
        IntentAnalysis latest = intentAnalysisService.latestByConversation(conversationId);
        if (latest == null) {
            return new TransferEvaluation(true, null, null, "尚无意向判定，建议人工介入");
        }
        boolean should = "other".equals(latest.getIntent())
                || (latest.getConfidence() != null && latest.getConfidence().doubleValue() < LOW_CONFIDENCE);
        String reason = should
                ? ("意向=" + latest.getIntent() + ", 置信度=" + latest.getConfidence() + "，建议转人工")
                : "意向明确（" + latest.getIntent() + "），AI 可继续接待";
        return new TransferEvaluation(should, latest.getIntent(), latest.getConfidence(), reason);
    }

    private String generateReply(Conversation conversation, String customerMessage, IntentAnalysis intent) {
        List<Message> recent = messageMapper.selectList(new LambdaQueryWrapper<Message>()
                .eq(Message::getConversationId, conversation.getId())
                .orderByDesc(Message::getId)
                .last("LIMIT " + CONTEXT_MESSAGE_LIMIT));
        if (recent.isEmpty()) {
            return suggestReply(intent == null ? null : intent.getIntent());
        }
        List<AiChatMessage> messages = recent.stream()
                .map(m -> new AiChatMessage(m.getSenderType(), m.getContent()))
                .toList();
        AiAnswerResponse resp = aiServiceClient.answer(new AiAnswerRequest(
                conversation.getTenantId(), conversation.getId(), messages, List.of(), List.of()));
        if (resp != null && StringUtils.hasText(resp.answer())) {
            return resp.answer();
        }
        // AI 不可用降级：规则话术
        log.warn("AI 回复降级 conversationId={}", conversation.getId());
        return suggestReply(intent == null ? null : intent.getIntent());
    }
}
