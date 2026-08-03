package com.aicrm.module.intent.service.impl;

import com.aicrm.module.ai.client.AiServiceClient;
import com.aicrm.module.ai.dto.AiChatMessage;
import com.aicrm.module.ai.dto.AiIntentResponse;
import com.aicrm.module.conversation.entity.Conversation;
import com.aicrm.module.conversation.entity.Message;
import com.aicrm.module.conversation.mapper.MessageMapper;
import com.aicrm.module.conversation.service.ConversationService;
import com.aicrm.module.intent.entity.IntentAnalysis;
import com.aicrm.module.intent.mapper.IntentAnalysisMapper;
import com.aicrm.module.intent.service.IntentAnalysisService;
import com.aicrm.module.lead.entity.Lead;
import com.aicrm.module.lead.service.LeadService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 意向分析服务实现：调用 AI 分类 → 落库 → 回写线索意向
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IntentAnalysisServiceImpl implements IntentAnalysisService {

    /** 参与意向分析的最近消息数 */
    private static final int RECENT_MESSAGE_LIMIT = 10;

    private final AiServiceClient aiServiceClient;
    private final MessageMapper messageMapper;
    private final IntentAnalysisMapper intentAnalysisMapper;
    private final ConversationService conversationService;
    private final LeadService leadService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IntentAnalysis analyzeByConversation(Long conversationId) {
        Conversation conversation = conversationService.getById(conversationId);
        if (conversation == null) {
            return null;
        }
        // 取最近消息
        List<Message> recent = messageMapper.selectList(new LambdaQueryWrapper<Message>()
                .eq(Message::getConversationId, conversationId)
                .orderByDesc(Message::getId)
                .last("LIMIT " + RECENT_MESSAGE_LIMIT));
        if (recent.isEmpty()) {
            return null;
        }
        // 按时间正序组装
        List<AiChatMessage> messages = recent.stream()
                .map(m -> new AiChatMessage(m.getSenderType(), m.getContent()))
                .toList();

        AiIntentResponse resp = aiServiceClient.classifyIntent(conversation.getTenantId(), conversationId, messages);
        if (resp == null) {
            // AI 不可用：降级返回空，不阻塞会话（后续可转人工兜底）
            log.warn("意向分析降级：AI 服务不可用 conversationId={}", conversationId);
            return null;
        }

        // 落库意向分析
        IntentAnalysis analysis = new IntentAnalysis();
        analysis.setTenantId(conversation.getTenantId());
        analysis.setConversationId(conversationId);
        analysis.setIntent(resp.intent());
        analysis.setConfidence(BigDecimal.valueOf(resp.confidence()));
        analysis.setModelVersion(resp.modelVersion());
        analysis.setEvidence(resp.evidence());
        intentAnalysisMapper.insert(analysis);

        // 回写线索意向
        if (conversation.getLeadId() != null) {
            Lead lead = leadService.getById(conversation.getLeadId());
            if (lead != null && !resp.intent().equals(lead.getIntent())) {
                lead.setIntent(resp.intent());
                leadService.updateById(lead);
            }
        }
        log.info("意向分析完成 conversationId={}, intent={}, confidence={}",
                conversationId, resp.intent(), resp.confidence());
        return analysis;
    }

    @Override
    public IntentAnalysis latestByConversation(Long conversationId) {
        return intentAnalysisMapper.selectOne(new LambdaQueryWrapper<IntentAnalysis>()
                .eq(IntentAnalysis::getConversationId, conversationId)
                .orderByDesc(IntentAnalysis::getId)
                .last("LIMIT 1"));
    }
}
