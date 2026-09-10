package com.aicrm.trade.contract.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/** Immutable commercial snapshot root created from one approved quote version. */
public record Contract(long id, long tenantId, String contractNo, String name, long quoteId, long quoteVersionId,
                       long customerId, String currency, BigDecimal subtotal, BigDecimal discountAmount,
                       BigDecimal taxAmount, BigDecimal totalAmount, Status status, LocalDate effectiveFrom,
                       LocalDate effectiveTo, Instant submittedAt, Instant signedAt, Instant voidedAt,
                       String voidReason, long version, Instant createdAt, Instant updatedAt) {
    public enum Status { DRAFT, PENDING_SIGNATURE, SIGNED, VOIDED }
}
