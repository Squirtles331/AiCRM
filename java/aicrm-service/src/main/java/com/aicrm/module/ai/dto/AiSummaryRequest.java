package com.aicrm.module.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * 对话摘要请求
 */
public record AiSummaryRequest(
        @JsonProperty("tenant_id") long tenantId,
        @JsonProperty("conversation_id") long conversationId,
        @JsonProperty("messages") List<AiChatMessage> messages) {
}
