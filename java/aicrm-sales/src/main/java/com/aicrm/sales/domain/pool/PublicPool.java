package com.aicrm.sales.domain.pool;

import java.time.Instant;

/** Immutable public-pool rule snapshot read from the database. */
public record PublicPool(long id, long tenantId, ResourceType resourceType, String code, String name,
                         boolean active, boolean autoRecycleEnabled, Integer recycleAfterDays,
                         boolean claimEnabled, boolean assignEnabled, boolean releaseEnabled,
                         int ruleVersion, Instant effectiveFrom) {
    public enum ResourceType {
        LEAD, CUSTOMER
    }
}
