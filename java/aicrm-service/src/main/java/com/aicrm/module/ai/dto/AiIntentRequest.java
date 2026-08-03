package com.aicrm.module.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * 意向分类请求
 */
public record AiIntentRequest(
        @JsonProperty("tenant_id") long tenantId,
        @JsonProperty("conversation_id") long conversationId,
        @JsonProperty("messages") List<AiChatMessage> messages) {
}
