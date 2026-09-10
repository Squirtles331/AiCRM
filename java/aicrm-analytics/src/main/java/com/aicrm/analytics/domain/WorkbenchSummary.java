package com.aicrm.analytics.domain;

import java.time.LocalDate;

/** Tenant-scoped, deterministic CRM workbench counters. */
public record WorkbenchSummary(
        LocalDate from,
        LocalDate to,
        long leadsCreated,
        long customersCreated,
        long openOpportunities,
        long opportunitiesWon,
        long opportunitiesLost,
        long quotesSubmitted,
        long quotesApproved,
        long contractsSigned,
        long ordersConfirmed,
        long ordersCancelled,
        long ordersClosed,
        long overdueFollowUps) {
}
