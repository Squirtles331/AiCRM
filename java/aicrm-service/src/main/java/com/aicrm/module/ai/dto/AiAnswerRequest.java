package com.aicrm.module.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * RAG 问答请求
 */
public record AiAnswerRequest(
        @JsonProperty("tenant_id") long tenantId,
        @JsonProperty("conversation_id") long conversationId,
        @JsonProperty("messages") List<AiChatMessage> messages,
        @JsonProperty("knowledge_docs") List<AiKnowledgeDoc> knowledgeDocs,
        @JsonProperty("product_docs") List<AiKnowledgeDoc> productDocs) {
}
