package com.aicrm.analytics.target.application;

import com.aicrm.analytics.target.domain.SalesTarget;
import java.math.BigDecimal;
import java.time.LocalDate;

public final class SalesTargetCommands {
    private SalesTargetCommands() { }
    public record Create(String name, long targetUserId, SalesTarget.Metric metric, LocalDate periodFrom, LocalDate periodTo,
                         BigDecimal targetValue, Long scoreRuleId) { }
    public record Versioned(long version) { }
}
