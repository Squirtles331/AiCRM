package com.aicrm.trade.order.application;

import java.time.Instant;

public final class SalesOrderCommands {
    private SalesOrderCommands() { }
    public record Create(long contractId, Instant expectedDeliveryAt) { }
}
