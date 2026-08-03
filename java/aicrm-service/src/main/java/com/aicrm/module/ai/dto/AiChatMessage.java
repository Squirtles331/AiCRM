package com.aicrm.module.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * AI 对话消息
 */
public record AiChatMessage(
        @JsonProperty("role") String role,
        @JsonProperty("content") String content) {
}
