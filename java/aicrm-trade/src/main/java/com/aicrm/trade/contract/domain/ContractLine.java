package com.aicrm.trade.contract.domain;

import java.math.BigDecimal;
import java.time.Instant;

/** Copy of an approved quote line. Product and price changes never rewrite this record. */
public record ContractLine(long id, long tenantId, long contractId, int lineNo, long quoteLineId, long productId,
                           String productNo, String sku, String productName, String unit, BigDecimal quantity,
                           BigDecimal listPrice, BigDecimal unitPrice, BigDecimal discountRate, BigDecimal taxRate,
                           BigDecimal lineAmount, Instant createdAt, Instant updatedAt) {
}
