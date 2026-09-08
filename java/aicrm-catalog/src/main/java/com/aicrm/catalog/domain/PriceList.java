package com.aicrm.catalog.domain;

import java.time.Instant;

public record PriceList(long id, long tenantId, String code, String name, String currency, Status status,
                        Instant effectiveFrom, Instant effectiveTo, long version, Instant createdAt, Instant updatedAt) {
    public enum Status { DRAFT, ACTIVE, EXPIRED, DISABLED }
}
