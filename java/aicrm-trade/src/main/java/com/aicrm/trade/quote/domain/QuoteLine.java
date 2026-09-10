package com.aicrm.trade.quote.domain;

import java.math.BigDecimal;
import java.time.Instant;

public record QuoteLine(long id, long tenantId, long quoteVersionId, int lineNo, long productId, long priceItemId,
                        String productNo, String sku, String productName, String unit, BigDecimal quantity,
                        BigDecimal listPrice, BigDecimal minimumPrice, BigDecimal unitPrice,
                        BigDecimal discountRate, BigDecimal taxRate, BigDecimal lineAmount,
                        Instant createdAt, Instant updatedAt) {
}
