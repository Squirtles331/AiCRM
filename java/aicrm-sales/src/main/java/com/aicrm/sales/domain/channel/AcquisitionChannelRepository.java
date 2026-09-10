package com.aicrm.sales.domain.channel;

import java.util.Optional;
import java.util.List;

public interface AcquisitionChannelRepository {
    AcquisitionChannel insert(AcquisitionChannel channel, long actorId);
    Optional<AcquisitionChannel> find(long tenantId, long channelId);
    List<AcquisitionChannel> list(long tenantId);
    boolean changeStatus(long tenantId, long channelId, long expectedVersion,
                         AcquisitionChannelStatus from, AcquisitionChannelStatus to, long actorId);
}
