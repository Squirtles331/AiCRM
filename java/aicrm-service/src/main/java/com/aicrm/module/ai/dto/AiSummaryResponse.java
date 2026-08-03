package com.aicrm.module.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 对话摘要响应
 */
public record AiSummaryResponse(
        @JsonProperty("summary") String summary,
        @JsonProperty("model_version") String modelVersion) {
}
