package com.aicrm.integration.domain;

import java.util.Optional;

public interface ConnectorRepository {
    Optional<Connector> find(long tenantId, long connectorId);

    Optional<Connector> findAnyTenant(long connectorId);

    Connector insert(Connector connector, long actorUserId);

    boolean updateStatus(long tenantId, long connectorId, long version, ConnectorStatus status, long actorUserId);

    Optional<ConnectorEvent> findEvent(long tenantId, long connectorId, String externalEventId);

    boolean insertEvent(ConnectorEvent event, long actorUserId);
}
