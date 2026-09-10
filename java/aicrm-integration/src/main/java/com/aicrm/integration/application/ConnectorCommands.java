package com.aicrm.integration.application;

import com.aicrm.integration.domain.ConnectorType;

/** HTTP-independent command inputs for connector administration and delivery. */
public final class ConnectorCommands {
    private ConnectorCommands() {
    }

    public record Create(String name, ConnectorType type, long operatorUserId, Long publicPoolId, String sharedSecret) {
    }

    public record StatusChange(long version) {
    }
}
