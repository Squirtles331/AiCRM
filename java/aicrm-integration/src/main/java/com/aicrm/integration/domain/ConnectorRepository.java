package com.aicrm.integration.domain;

import java.util.Optional;
import java.time.Instant;

public interface ConnectorRepository {
    Optional<Connector> find(long tenantId, long connectorId);

    Optional<Connector> findAnyTenant(long connectorId);

    Connector insert(Connector connector, long actorUserId);

    boolean updateStatus(long tenantId, long connectorId, long version, ConnectorStatus status, long actorUserId);

    Optional<ConnectorEvent> findEvent(long tenantId, long connectorId, String externalEventId);

    boolean insertEvent(ConnectorEvent event, long actorUserId);

    ConnectorMonitoring monitoring(long tenantId, long connectorId, Instant from, Instant to);

    boolean ownsConnectorEventOutbox(long tenantId, long connectorId, long outboxEventId);
}
