package com.aicrm.catalog.domain;

import java.time.Instant;

public record Product(long id, long tenantId, Long categoryId, String productNo, String sku, String name,
                      String specification, String unit, Status status, boolean saleEnabled,
                      long version, Instant createdAt, Instant updatedAt) {
    public enum Status { ACTIVE, DISABLED }
}
