package com.aicrm.platform.application;

import java.time.Instant;

/** Immutable event envelope loaded from the transactional outbox. */
public record OutboxMessage(long id, long tenantId, String aggregateType, long aggregateId,
                            String eventType, int eventVersion, String operationId, String traceId,
                            String payload, Instant occurredAt, int retryCount) {
}
