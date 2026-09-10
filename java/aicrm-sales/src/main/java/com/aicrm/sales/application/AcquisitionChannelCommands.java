package com.aicrm.sales.application;

public final class AcquisitionChannelCommands {
    private AcquisitionChannelCommands() { }
    public record Create(String code, String name, String sourceType) { }
    public record Versioned(long version) { }
}
