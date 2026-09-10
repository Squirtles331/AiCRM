package com.aicrm.sales.domain.conversation;
import java.time.Instant;
public record SalesConversationEntry(long id,long tenantId,long conversationId,Direction direction,String content,Instant occurredAt,long createdBy,Instant createdAt) { public enum Direction { INBOUND,OUTBOUND,NOTE } }
