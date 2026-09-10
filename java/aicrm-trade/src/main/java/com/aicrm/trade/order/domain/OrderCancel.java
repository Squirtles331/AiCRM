package com.aicrm.trade.order.domain;

import java.time.Instant;

public record OrderCancel(long id, long tenantId, long orderId, String cancelNo, Status status, String reason,
                          String requestSnapshot, Instant requestedAt, Instant approvedAt, Instant rejectedAt,
                          Instant completedAt, String rejectionReason, long version, long createdBy,
                          Instant createdAt, Instant updatedAt) {
    public enum Status { PENDING, APPROVED, REJECTED, COMPLETED }
}
