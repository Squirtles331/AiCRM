package com.aicrm.analytics.target.domain;

import java.math.BigDecimal;
import java.time.Instant;

/** Immutable CRM-only calculation confirmed after the target period closes. */
public record SalesTargetResult(long id, long targetId, BigDecimal actualValue, BigDecimal achievementRate,
                                String calculationSnapshot, long confirmedBy, Instant calculatedAt, Instant confirmedAt) {
}
