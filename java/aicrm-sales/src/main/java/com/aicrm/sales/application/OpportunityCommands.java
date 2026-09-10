package com.aicrm.sales.application;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Commands for the opportunity pipeline, independent from HTTP and database records. */
public final class OpportunityCommands {
    private OpportunityCommands() { }

    public record Create(long customerId, Long contactId, Long sourceLeadId, String name, BigDecimal expectedAmount,
                         String currency, short probability, LocalDate expectedCloseDate) { }
    public record ChangeStage(long version, String stage, short probability) { }
}
