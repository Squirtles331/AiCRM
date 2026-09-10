package com.aicrm.analytics.performance.application;

import com.aicrm.analytics.target.domain.SalesTarget;

import java.math.BigDecimal;
import java.util.List;

public final class PerformanceScoreRuleCommands {
    private PerformanceScoreRuleCommands() { }
    public record Band(BigDecimal minimumAchievementRate, BigDecimal maximumAchievementRate, BigDecimal score) { }
    public record Create(String name, SalesTarget.Metric metric, List<Band> bands) { }
    public record Versioned(long version) { }
}
