package com.aicrm.trade.quote.application;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public final class QuoteCommands {
    private QuoteCommands() { }

    public record Line(long productId, long priceItemId, BigDecimal quantity, BigDecimal unitPrice,
                       BigDecimal discountRate) { }
    public record Create(long opportunityId, long priceListId, LocalDate validUntil, List<Line> lines) { }
    public record AddVersion(long expectedQuoteVersion, LocalDate validUntil, List<Line> lines) { }
}
