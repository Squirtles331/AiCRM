package com.aicrm.web.analytics.performance;

import com.aicrm.analytics.performance.domain.PerformanceScoreBand;
import com.aicrm.analytics.performance.domain.PerformanceScoreRule;
import com.aicrm.analytics.target.domain.SalesTarget;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

final class PerformanceScoreRuleApiDtos {
    private PerformanceScoreRuleApiDtos() { }
    record CreateRequest(@NotBlank String name, @NotNull SalesTarget.Metric metric, @NotEmpty List<@Valid BandRequest> bands) { }
    record BandRequest(@NotNull BigDecimal minimumAchievementRate, BigDecimal maximumAchievementRate, @NotNull BigDecimal score) { }
    record VersionRequest(@NotNull Long version) { }
    record View(String id, String ruleNo, String name, String metric, String status, long version, Instant createdAt, Instant updatedAt, List<BandView> bands) { }
    record BandView(String id, int bandNo, BigDecimal minimumAchievementRate, BigDecimal maximumAchievementRate, BigDecimal score) { }
    static View view(PerformanceScoreRule rule,List<PerformanceScoreBand> bands) { return new View(String.valueOf(rule.id()),rule.ruleNo(),rule.name(),rule.metric().name(),rule.status().name(),rule.version(),rule.createdAt(),rule.updatedAt(),bands.stream().map(band->new BandView(String.valueOf(band.id()),band.bandNo(),band.minimumAchievementRate(),band.maximumAchievementRate(),band.score())).toList()); }
}
