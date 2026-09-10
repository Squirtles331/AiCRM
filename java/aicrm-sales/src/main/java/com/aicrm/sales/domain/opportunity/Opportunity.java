package com.aicrm.sales.domain.opportunity;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/** Sales opportunity aggregate. Its customer is required, while contact and source lead are optional context links. */
public record Opportunity(long id, long tenantId, String opportunityNo, String name, long customerId, Long contactId,
                          Long sourceLeadId, Stage stage, Status status, BigDecimal expectedAmount, String currency,
                          short probability, LocalDate expectedCloseDate, long ownerUserId, long ownerDeptId,
                          String lostReason, Instant lostAt, Instant wonAt, long version, Instant createdAt, Instant updatedAt) {
    public enum Stage { DISCOVERY, QUALIFICATION, SOLUTION, QUOTATION, NEGOTIATION, CLOSED_WON, CLOSED_LOST }
    public enum Status { OPEN, WON, LOST }
}
