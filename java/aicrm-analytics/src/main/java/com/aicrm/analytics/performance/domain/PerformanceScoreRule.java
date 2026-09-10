package com.aicrm.analytics.performance.domain;

import com.aicrm.analytics.target.domain.SalesTarget;

import java.time.Instant;

/** Tenant-owned, versioned CRM scoring rule. Active and retired rules are immutable. */
public record PerformanceScoreRule(long id, long tenantId, String ruleNo, String name, SalesTarget.Metric metric,
                                   Status status, long version, Instant createdAt, Instant updatedAt) {
    public enum Status { DRAFT, ACTIVE, RETIRED }
}
