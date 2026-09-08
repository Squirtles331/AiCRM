package com.aicrm.catalog.domain;

import java.math.BigDecimal;
import java.time.Instant;

public record PriceItem(long id, long tenantId, long priceListId, long productId, BigDecimal listPrice,
                        BigDecimal minimumPrice, BigDecimal taxRate, Status status, long version,
                        Instant createdAt, Instant updatedAt) {
    public enum Status { ACTIVE, DISABLED }
}
