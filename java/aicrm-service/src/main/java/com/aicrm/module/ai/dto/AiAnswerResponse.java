package com.aicrm.module.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * RAG 问答响应
 */
public record AiAnswerResponse(
        @JsonProperty("answer") String answer,
        @JsonProperty("quoted_doc_ids") List<Long> quotedDocIds,
        @JsonProperty("confidence") double confidence,
        @JsonProperty("model_version") String modelVersion) {
}
