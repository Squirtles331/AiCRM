package com.aicrm.integration.domain;

import java.time.Instant;

/** Tenant-scoped configuration. The stored value is a one-way shared-secret hash. */
public record Connector(long id, long tenantId, String connectorNo, String name, ConnectorType type,
                        ConnectorStatus status, long operatorUserId, Long publicPoolId, String secretHash,
                        long version, Instant createdAt, Instant updatedAt) {
}
