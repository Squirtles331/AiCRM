package com.aicrm.analytics.report.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Time-bounded, CRM-only sales outcome metrics grouped by the current opportunity owner. */
public record SalesPerformanceReport(LocalDate from, LocalDate to, List<Owner> owners) {
    public record Owner(String ownerUserId, long opportunitiesWon, BigDecimal wonExpectedAmount,
                        long contractsSigned, BigDecimal signedContractAmount,
                        long ordersConfirmed, BigDecimal confirmedOrderAmount) { }
}
