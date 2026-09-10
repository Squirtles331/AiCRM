package com.aicrm.trade.contract.application;

import java.time.LocalDate;

public final class ContractCommands {
    private ContractCommands() { }
    public record Create(long quoteId, String name, LocalDate effectiveFrom, LocalDate effectiveTo) { }
    public record Versioned(long version) { }
}
