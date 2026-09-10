package com.aicrm.trade.quote.domain;

import java.math.BigDecimal;
import java.time.Instant;

public record QuoteVersion(long id, long tenantId, long quoteId, int versionNo, Status status,
                           BigDecimal subtotal, BigDecimal discountAmount, BigDecimal taxAmount, BigDecimal totalAmount,
                           BigDecimal discountRate, String rejectionReason, Instant submittedAt, Instant approvedAt,
                           Instant rejectedAt, Instant expiredAt, long version, Instant createdAt, Instant updatedAt) {
    public enum Status { DRAFT, SUBMITTED, APPROVED, REJECTED, EXPIRED }
}
