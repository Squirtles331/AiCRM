package com.aicrm.web.sales;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.Instant;
import java.util.List;

final class SalesApiDtos {
    private SalesApiDtos() {
    }

    record CreateLeadRequest(@NotBlank String name, String mobile, String email, String companyName,
                             @NotBlank String sourceType, String sourceRef, String intent, Long publicPoolId) {
    }

    record CreateCustomerRequest(@NotBlank String name, String industry, String region, Long publicPoolId) {
    }

    record OwnershipRequest(@NotNull Long version, Long targetUserId, Long publicPoolId, String reason) {
    }

    record InvalidateRequest(@NotNull Long version, @NotBlank String reason) {
    }

    record ConvertRequest(@NotNull Long version, Long existingCustomerId, String customerName, String industry, String region) {
    }

    record MergeRequest(@NotNull Long version, @NotNull Long targetCustomerId, String reason) {
    }

    record ContactRequest(@NotBlank String name, String mobile, String email, String department, String title,
                          boolean decisionMaker) {
    }

    record FollowUpRequest(@NotNull Long version, @NotBlank String channel, @NotBlank String content, Instant nextFollowUpAt) {
    }

    record HandoverRequest(@NotBlank String resourceType, @NotNull Long resourceId, @NotNull Long version,
                           @NotNull Long fromUserId, @NotNull Long toUserId, String reason) {
    }

    record BatchHandoverRequest(@NotBlank String batchNo, @NotNull Long fromUserId, @NotNull Long toUserId,
                                @Min(0) @Max(500) int pageSize, String reason) {
    }

    record PageView<T>(List<T> items, long page, long size, long total) {
    }

    record LeadView(String id, String leadNo, String name, String mobile, String email, String companyName,
                    String sourceType, String sourceRef, String intent, String status, String ownershipType,
                    String ownerUserId, String ownerDeptId, String publicPoolId, String customerId,
                    Instant poolEnteredAt, Instant lastFollowUpAt, Instant nextFollowUpAt, long version,
                    Instant createdAt, Instant updatedAt) {
    }

    record CustomerView(String id, String customerNo, String name, String industry, String region, String status,
                        String ownershipType, String ownerUserId, String ownerDeptId, String publicPoolId,
                        Instant poolEnteredAt, Instant lastFollowUpAt, Instant nextFollowUpAt, long version,
                        Instant createdAt, Instant updatedAt) {
    }

    record ContactView(String id, String customerId, String name, String mobile, String email, String department,
                       String title, boolean decisionMaker, long version) {
    }
}
