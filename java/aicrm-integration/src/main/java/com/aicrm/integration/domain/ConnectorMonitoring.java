package com.aicrm.integration.domain;

import java.time.Instant;

/** Read model for a connector's accepted events and their Outbox publication state. */
public record ConnectorMonitoring(long receivedEvents, long leadsCreated, long acceptedEvents,
                                  long pendingPublications, long publishedPublications, long deadPublications,
                                  DeliveryIssue latestIssue) {
    public record DeliveryIssue(long outboxEventId, String status, int retryCount, String lastError, Instant updatedAt) {
    }
}
