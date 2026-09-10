package com.aicrm.web.trade.order;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

final class SalesOrderApiDtos {
    private SalesOrderApiDtos() { }
    record CreateRequest(@NotNull Long contractId, Instant expectedDeliveryAt) { }
    record VersionRequest(@NotNull Long version) { }
    record ReasonRequest(@NotNull Long version, @jakarta.validation.constraints.NotBlank String reason) { }
    record CancelDecisionRequest(@NotNull Long orderVersion, @NotNull Long cancellationVersion, String reason) { }
    record OrderView(String id, String orderNo, String contractId, String customerId, String currency, BigDecimal totalAmount, String status, Instant expectedDeliveryAt, Instant confirmedAt, Instant cancelledAt, Instant closedAt, String closeReason, long version) { }
    record LineView(String id, int lineNo, String contractLineId, String productId, String productNo, String sku, String productName, String unit, BigDecimal quantity, BigDecimal unitPrice, BigDecimal taxRate, BigDecimal lineAmount) { }
    record CancellationView(String id, String cancelNo, String orderId, String status, String reason, String requestSnapshot,
                            String rejectionReason, Instant requestedAt, Instant completedAt, long version) { }
}
