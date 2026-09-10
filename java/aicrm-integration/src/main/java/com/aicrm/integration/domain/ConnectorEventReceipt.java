package com.aicrm.integration.domain;

import java.time.Instant;

public record ConnectorEventReceipt(long eventId, String externalEventId, String outcome, Long leadId,
                                    Instant receivedAt, boolean replayed) {
    public static ConnectorEventReceipt from(ConnectorEvent event, boolean replayed) {
        return new ConnectorEventReceipt(event.id(), event.externalEventId(), event.outcome().name(), event.leadId(),
                event.receivedAt(), replayed);
    }
}
