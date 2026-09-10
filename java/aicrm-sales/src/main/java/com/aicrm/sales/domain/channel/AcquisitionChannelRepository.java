package com.aicrm.sales.domain.channel;

import java.util.Optional;

public interface AcquisitionChannelRepository {
    AcquisitionChannel insert(AcquisitionChannel channel, long actorId);
    Optional<AcquisitionChannel> find(long tenantId, long channelId);
    boolean changeStatus(long tenantId, long channelId, long expectedVersion,
                         AcquisitionChannelStatus from, AcquisitionChannelStatus to, long actorId);
}
