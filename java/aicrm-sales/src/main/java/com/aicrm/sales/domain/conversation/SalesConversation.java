package com.aicrm.sales.domain.conversation;
import java.time.Instant;
public record SalesConversation(long id,long tenantId,long customerId,Long contactId,String subject,String channel,Status status,String summary,long ownerUserId,Instant closedAt,long version,Instant createdAt,Instant updatedAt) { public enum Status { OPEN,CLOSED } }
