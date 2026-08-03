package com.aicrm.module.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * 实体抽取请求
 */
public record AiExtractRequest(
        @JsonProperty("tenant_id") long tenantId,
        @JsonProperty("lead_id") long leadId,
        @JsonProperty("conversation_id") long conversationId,
        @JsonProperty("messages") List<AiChatMessage> messages) {
}
