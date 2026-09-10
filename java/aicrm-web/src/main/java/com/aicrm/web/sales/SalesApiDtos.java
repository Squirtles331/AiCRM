package com.aicrm.web.sales;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
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

    record CreateOpportunityRequest(@NotNull Long customerId, Long contactId, Long sourceLeadId, @NotBlank String name,
                                    @NotNull @DecimalMin("0.00") BigDecimal expectedAmount, @NotBlank String currency,
                                    @Min(0) @Max(100) short probability, LocalDate expectedCloseDate) {
    }

    record ChangeOpportunityStageRequest(@NotNull Long version, @NotBlank String stage,
                                         @Min(0) @Max(100) short probability) {
    }

    record OpportunityVersionRequest(@NotNull Long version) {
    }

    record LoseOpportunityRequest(@NotNull Long version, @NotBlank String reason) {
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

    record OpportunityView(String id, String opportunityNo, String name, String customerId, String contactId,
                           String sourceLeadId, String stage, String status, BigDecimal expectedAmount, String currency,
                           short probability, LocalDate expectedCloseDate, String ownerUserId, String ownerDeptId,
                           String lostReason, Instant lostAt, Instant wonAt, long version, Instant createdAt, Instant updatedAt) {
    }

    record OpportunityStageHistoryView(String id, String opportunityId, String action, String fromStage, String toStage,
                                       String fromStatus, String toStatus, Short fromProbability, short toProbability,
                                       String reason, String operatorUserId, Instant createdAt) {
    }
}
