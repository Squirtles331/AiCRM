package com.aicrm.module.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 抽取字段
 */
public record AiExtractedField(
        @JsonProperty("field_key") String fieldKey,
        @JsonProperty("field_value") String fieldValue,
        @JsonProperty("confidence") double confidence) {
}
