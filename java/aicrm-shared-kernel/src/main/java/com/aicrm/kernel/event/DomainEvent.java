package com.aicrm.kernel.event;

import java.time.Instant;

/** Persisted through the outbox before it is published to a broker. */
public record DomainEvent(String eventType, String aggregateType, long aggregateId,
                          long tenantId, String payload, Instant occurredAt) {
}
