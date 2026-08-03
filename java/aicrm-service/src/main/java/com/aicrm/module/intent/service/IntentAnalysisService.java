package com.aicrm.module.intent.service;

import com.aicrm.module.intent.entity.IntentAnalysis;

/**
 * 意向分析服务
 */
public interface IntentAnalysisService {

    /**
     * 分析会话最近消息的意向，落库并回写线索
     *
     * @param conversationId 会话 ID
     * @return 分析结果；AI 服务不可用时返回 null（调用方决定降级）
     */
    IntentAnalysis analyzeByConversation(Long conversationId);

    /**
     * 查询会话最近一次意向分析结果
     *
     * @param conversationId 会话 ID
     * @return 最近意向；无记录返回 null
     */
    IntentAnalysis latestByConversation(Long conversationId);
}
