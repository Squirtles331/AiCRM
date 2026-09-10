package com.aicrm.trade.contract.application;

import java.time.LocalDate;

public final class ContractCommands {
    private ContractCommands() { }
    public record Create(long quoteId, String name, LocalDate effectiveFrom, LocalDate effectiveTo) { }
    public record Versioned(long version) { }
    public record Reasoned(long version, String reason) { }
    public record CreateChange(long contractVersion, String reason, String proposedName, LocalDate proposedEffectiveFrom,
                               LocalDate proposedEffectiveTo) { }
    public record RejectChange(long changeVersion, String reason) { }
}
