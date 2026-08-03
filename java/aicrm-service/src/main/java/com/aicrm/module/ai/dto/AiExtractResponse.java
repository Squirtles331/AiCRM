package com.aicrm.module.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * 实体抽取响应
 */
public record AiExtractResponse(
        @JsonProperty("fields") List<AiExtractedField> fields,
        @JsonProperty("model_version") String modelVersion) {
}
