package com.aicrm.trade.order.domain;

import java.math.BigDecimal;
import java.time.Instant;

public record SalesOrder(long id, long tenantId, String orderNo, long contractId, long customerId, String externalOrderNo,
                         String currency, BigDecimal totalAmount, Status status, Instant expectedDeliveryAt,
                         Instant confirmedAt, Instant cancelledAt, Instant closedAt, String closeReason, long version,
                         Instant createdAt, Instant updatedAt) {
    public enum Status { DRAFT, CONFIRMED, CANCELLING, CANCELLED, CLOSED }
}
