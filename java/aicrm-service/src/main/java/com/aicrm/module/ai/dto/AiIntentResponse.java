package com.aicrm.module.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 意向分类响应
 */
public record AiIntentResponse(
        @JsonProperty("intent") String intent,
        @JsonProperty("confidence") double confidence,
        @JsonProperty("evidence") String evidence,
        @JsonProperty("model_version") String modelVersion) {
}
