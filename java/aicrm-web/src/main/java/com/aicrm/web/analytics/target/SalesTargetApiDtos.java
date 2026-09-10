package com.aicrm.web.analytics.target;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import com.aicrm.analytics.target.domain.SalesTarget;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

final class SalesTargetApiDtos {
    private SalesTargetApiDtos() { }
    record CreateRequest(@NotBlank String name, @NotNull Long targetUserId, @NotNull SalesTarget.Metric metric, @NotNull LocalDate periodFrom,
                         @NotNull LocalDate periodTo, @NotNull @Positive BigDecimal targetValue, Long scoreRuleId) { }
    record VersionRequest(@NotNull Long version) { }
    record View(String id, String targetNo, String name, String targetUserId, String metric, LocalDate periodFrom, LocalDate periodTo,
                BigDecimal targetValue, String scoreRuleId, String status, long version, Instant createdAt, Instant updatedAt, ResultView result) { }
    record ResultView(String id, BigDecimal actualValue, BigDecimal achievementRate, String calculationSnapshot, String confirmedBy,
                      Instant calculatedAt, Instant confirmedAt, ScoreView score) { }
    record ScoreView(String id, String scoreRuleId, BigDecimal score, BigDecimal achievementRate, String calculationSnapshot, String confirmedBy, Instant confirmedAt) { }
}
