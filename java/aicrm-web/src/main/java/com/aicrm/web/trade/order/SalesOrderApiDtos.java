package com.aicrm.web.trade.order;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

final class SalesOrderApiDtos {
    private SalesOrderApiDtos() { }
    record CreateRequest(@NotNull Long contractId, Instant expectedDeliveryAt) { }
    record OrderView(String id, String orderNo, String contractId, String customerId, String currency, BigDecimal totalAmount, String status, Instant expectedDeliveryAt, long version) { }
    record LineView(String id, int lineNo, String contractLineId, String productId, String productNo, String sku, String productName, String unit, BigDecimal quantity, BigDecimal unitPrice, BigDecimal taxRate, BigDecimal lineAmount) { }
}
