package com.aicrm.trade.contract.domain;

import java.time.Instant;

/** Immutable record of a proposed amendment; approval never rewrites the signed contract. */
public record ContractChange(long id, long tenantId, long contractId, String changeNo, Status status, String reason,
                             String beforeSnapshot, String afterSnapshot, Instant submittedAt, Instant approvedAt,
                             Instant rejectedAt, Instant cancelledAt, String rejectionReason, long version,
                             long createdBy, Instant createdAt, Instant updatedAt) {
    public enum Status { DRAFT, SUBMITTED, APPROVED, REJECTED, CANCELLED }
}
