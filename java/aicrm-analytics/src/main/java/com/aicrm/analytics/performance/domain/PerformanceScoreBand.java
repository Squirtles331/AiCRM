package com.aicrm.analytics.performance.domain;

import java.math.BigDecimal;

/** [minimum, maximum) achievement-rate band; null maximum denotes the final unbounded band. */
public record PerformanceScoreBand(long id, int bandNo, BigDecimal minimumAchievementRate,
                                   BigDecimal maximumAchievementRate, BigDecimal score) { }
