package com.aicrm.trade.order.application;

import java.time.Instant;

public final class SalesOrderCommands {
    private SalesOrderCommands() { }
    public record Create(long contractId, Instant expectedDeliveryAt) { }
    public record Versioned(long version) { }
    public record Reasoned(long version, String reason) { }
    public record CancelDecision(long orderVersion, long cancellationVersion, String reason) { }
}
