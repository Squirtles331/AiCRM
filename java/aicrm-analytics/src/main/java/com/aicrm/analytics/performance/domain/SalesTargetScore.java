package com.aicrm.analytics.performance.domain;

import java.math.BigDecimal;
import java.time.Instant;

/** Immutable score derived from one immutable confirmed sales-target result. */
public record SalesTargetScore(long id, long targetId, long targetResultId, long scoreRuleId,
                               BigDecimal achievementRate, BigDecimal score, String calculationSnapshot,
                               long confirmedBy, Instant confirmedAt) { }
