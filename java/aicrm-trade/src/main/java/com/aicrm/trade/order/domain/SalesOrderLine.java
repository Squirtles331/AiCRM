package com.aicrm.trade.order.domain;

import java.math.BigDecimal;
import java.time.Instant;

public record SalesOrderLine(long id, long tenantId, long orderId, int lineNo, long contractLineId, long productId,
                             String productNo, String sku, String productName, String unit, BigDecimal quantity,
                             BigDecimal unitPrice, BigDecimal taxRate, BigDecimal lineAmount,
                             Instant createdAt, Instant updatedAt) { }
