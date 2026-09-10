package com.aicrm.web.sales;

import com.aicrm.sales.domain.channel.AcquisitionChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

final class AcquisitionChannelApiDtos {
    private AcquisitionChannelApiDtos() { }
    record CreateRequest(@NotBlank String code, @NotBlank String name, @NotBlank String sourceType) { }
    record VersionRequest(@NotNull Long version) { }
    record View(String id, String code, String name, String sourceType, String status, long version, Instant createdAt, Instant updatedAt) { }
    static View view(AcquisitionChannel value) { return new View(String.valueOf(value.id()), value.code(), value.name(), value.sourceType(), value.status().name(), value.version(), value.createdAt(), value.updatedAt()); }
}
