package com.aicrm.catalog.domain;

import java.time.Instant;

public record ProductCategory(long id, long tenantId, Long parentId, String code, String name,
                              Status status, int sortOrder, long version, Instant createdAt, Instant updatedAt) {
    public enum Status { ACTIVE, DISABLED }
}
