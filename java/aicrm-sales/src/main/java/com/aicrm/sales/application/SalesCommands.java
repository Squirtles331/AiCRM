package com.aicrm.sales.application;

import java.time.Instant;

/** Command input types are independent of both HTTP DTOs and persistence records. */
public final class SalesCommands {
    private SalesCommands() {
    }

    public record CreateLead(String name, String mobile, String email, String companyName,
                             String sourceType, String sourceRef, String intent, Long publicPoolId) {
    }

    public record CreateCustomer(String name, String industry, String region, Long publicPoolId) {
    }

    public record OwnershipChange(long resourceId, long version, Long targetUserId, Long publicPoolId, String reason) {
    }

    public record ConvertLead(long leadId, long version, Long existingCustomerId,
                              String customerName, String industry, String region) {
    }

    public record MergeCustomers(long sourceCustomerId, long targetCustomerId, long version, String reason) {
    }

    public record CreateContact(String name, String mobile, String email, String department, String title,
                                boolean decisionMaker) {
    }

    public record AddFollowUp(long version, String channel, String content, Instant nextFollowUpAt) {
    }

    public record Handover(long resourceId, long version, long fromUserId, long toUserId, String reason) {
    }
}
