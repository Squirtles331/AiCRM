package com.aicrm.analytics.target.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/** A personal CRM sales target with a fixed metric and period. */
public record SalesTarget(long id, long tenantId, String targetNo, String name, long targetUserId, Metric metric,
                          LocalDate periodFrom, LocalDate periodTo, BigDecimal targetValue, Long scoreRuleId, Status status,
                          long version, Instant createdAt, Instant updatedAt) {
    public enum Metric { SIGNED_CONTRACT_AMOUNT, CONFIRMED_ORDER_AMOUNT }
    public enum Status { DRAFT, ACTIVE, RESULT_CONFIRMED }
}
