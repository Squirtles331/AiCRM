package com.aicrm.sales.domain.customer;

import com.aicrm.sales.domain.OwnershipType;

import java.time.Instant;

/** Customer aggregate snapshot. Customer is the sales master data after lead conversion. */
public record Customer(long id, long tenantId, String customerNo, String name, String industry, String region,
                       String status, OwnershipType ownershipType, Long ownerUserId, Long ownerDeptId,
                       Long publicPoolId, Instant poolEnteredAt, Instant lastFollowUpAt,
                       Instant nextFollowUpAt, long version, Instant createdAt, Instant updatedAt) {
    public boolean isPrivateOwnedBy(long userId) {
        return ownershipType == OwnershipType.PRIVATE && ownerUserId != null && ownerUserId == userId;
    }
}
