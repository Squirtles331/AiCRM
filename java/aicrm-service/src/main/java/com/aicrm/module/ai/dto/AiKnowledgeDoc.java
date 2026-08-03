package com.aicrm.module.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * 知识片段（RAG 上下文）
 */
public record AiKnowledgeDoc(
        @JsonProperty("doc_id") long docId,
        @JsonProperty("title") String title,
        @JsonProperty("content") String content) {
}
