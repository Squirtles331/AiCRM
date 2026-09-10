package com.aicrm.sales.domain.channel;

import java.time.Instant;

/** CRM-owned source channel. Its code is copied onto new leads as historical attribution. */
public record AcquisitionChannel(long id, long tenantId, String code, String name, String sourceType,
                                 AcquisitionChannelStatus status, long version, Instant createdAt, Instant updatedAt) {
    public boolean active() { return status == AcquisitionChannelStatus.ACTIVE; }
}
