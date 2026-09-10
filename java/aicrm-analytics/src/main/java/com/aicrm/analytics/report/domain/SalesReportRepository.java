package com.aicrm.analytics.report.domain;

import com.aicrm.kernel.security.Actor;

import java.time.Instant;

public interface SalesReportRepository {
    SalesFunnelSnapshot funnel(Actor actor, Instant asOf);
    SalesPerformanceReport performance(Actor actor, java.time.LocalDate from, java.time.LocalDate to, Instant startInclusive, Instant endExclusive);
}
