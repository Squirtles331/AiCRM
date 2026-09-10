package com.aicrm.web.integration;

import com.aicrm.integration.domain.Connector;
import com.aicrm.integration.domain.ConnectorEventReceipt;
import com.aicrm.integration.domain.ConnectorType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import com.aicrm.integration.domain.ConnectorMonitoring;

final class ConnectorApiDtos {
    private ConnectorApiDtos() {
    }

    record CreateRequest(@NotBlank @Size(max = 200) String name, @NotNull ConnectorType type,
                         @NotNull Long operatorUserId, Long publicPoolId,
                         @NotBlank @Size(min = 16, max = 200) String sharedSecret) {
    }

    record VersionRequest(@NotNull Long version) {
    }

    record View(String id, String connectorNo, String name, String type, String status, String operatorUserId,
                String publicPoolId, long version, Instant createdAt, Instant updatedAt) {
    }

    record EventReceiptView(String eventId, String externalEventId, String outcome, String leadId,
                            Instant receivedAt, boolean replayed) {
    }

    record MonitoringView(long receivedEvents, long leadsCreated, long acceptedEvents, long pendingPublications,
                          long publishedPublications, long deadPublications, DeliveryIssueView latestIssue) {
    }

    record DeliveryIssueView(String outboxEventId, String status, int retryCount, String lastError, Instant updatedAt) {
    }

    record RetryReceiptView(String outboxEventId, String status, int retryCount) {
    }

    static View view(Connector connector) {
        return new View(String.valueOf(connector.id()), connector.connectorNo(), connector.name(), connector.type().name(),
                connector.status().name(), String.valueOf(connector.operatorUserId()), id(connector.publicPoolId()),
                connector.version(), connector.createdAt(), connector.updatedAt());
    }

    static EventReceiptView view(ConnectorEventReceipt receipt) {
        return new EventReceiptView(String.valueOf(receipt.eventId()), receipt.externalEventId(), receipt.outcome(),
                id(receipt.leadId()), receipt.receivedAt(), receipt.replayed());
    }

    static MonitoringView view(ConnectorMonitoring monitoring) {
        ConnectorMonitoring.DeliveryIssue issue = monitoring.latestIssue();
        return new MonitoringView(monitoring.receivedEvents(), monitoring.leadsCreated(), monitoring.acceptedEvents(),
                monitoring.pendingPublications(), monitoring.publishedPublications(), monitoring.deadPublications(),
                issue == null ? null : new DeliveryIssueView(String.valueOf(issue.outboxEventId()), issue.status(),
                        issue.retryCount(), issue.lastError(), issue.updatedAt()));
    }

    private static String id(Long value) {
        return value == null ? null : String.valueOf(value);
    }
}
