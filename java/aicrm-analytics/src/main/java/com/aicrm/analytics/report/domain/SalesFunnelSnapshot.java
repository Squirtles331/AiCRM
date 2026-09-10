package com.aicrm.analytics.report.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/** Current CRM pipeline snapshot; it deliberately does not reconstruct historic pipeline state. */
public record SalesFunnelSnapshot(Instant asOf, List<Stage> stages) {
    public record Stage(String stage, long opportunityCount, BigDecimal expectedAmount, BigDecimal weightedAmount) { }
}
