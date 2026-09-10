package com.aicrm.integration.domain;

import java.time.Instant;

/** Immutable receipt of an authenticated external event. */
public record ConnectorEvent(long id, long tenantId, long connectorId, String externalEventId, String eventType,
                             String payload, String payloadHash, Outcome outcome, Long leadId, Instant receivedAt) {
    public enum Outcome {
        LEAD_CREATED,
        ACCEPTED
    }
}
