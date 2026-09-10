package com.aicrm.sales.domain.lead;

import com.aicrm.sales.domain.OwnershipType;

import java.time.Instant;

/** Lead aggregate snapshot. Mutations are applied by the application service under version checks. */
public record Lead(long id, long tenantId, String leadNo, String name, String mobile, String email,
                   String companyName, String sourceType, String sourceRef, String intent, Long acquisitionChannelId,
                   String acquisitionChannelCode,
                   LeadStatus status, OwnershipType ownershipType, Long ownerUserId, Long ownerDeptId,
                   Long publicPoolId, Long customerId, Instant poolEnteredAt, Instant lastFollowUpAt,
                   Instant nextFollowUpAt, long version, Instant createdAt, Instant updatedAt) {
    public boolean isPrivateOwnedBy(long userId) {
        return ownershipType == OwnershipType.PRIVATE && ownerUserId != null && ownerUserId == userId;
    }
}
