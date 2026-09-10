package com.aicrm.trade.quote.domain;

import java.time.LocalDate;
import java.time.Instant;

public record Quote(long id, long tenantId, String quoteNo, long opportunityId, long customerId, long priceListId,
                    String currency, Status status, int currentVersionNo, LocalDate validUntil, long version,
                    Instant createdAt, Instant updatedAt) {
    public enum Status { DRAFT, SUBMITTED, APPROVED, REJECTED, EXPIRED, CANCELLED }
}
