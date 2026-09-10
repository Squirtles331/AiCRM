package com.aicrm.web.trade.contract;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

final class ContractApiDtos {
    private ContractApiDtos() { }
    record CreateRequest(@NotNull Long quoteId, @NotBlank String name, LocalDate effectiveFrom, LocalDate effectiveTo) { }
    record VersionRequest(@NotNull Long version) { }
    record ContractView(String id, String contractNo, String name, String quoteId, String quoteVersionId, String customerId, String currency,
                        BigDecimal subtotal, BigDecimal discountAmount, BigDecimal taxAmount, BigDecimal totalAmount, String status,
                        LocalDate effectiveFrom, LocalDate effectiveTo, Instant submittedAt, Instant signedAt, long version) { }
    record LineView(String id, int lineNo, String quoteLineId, String productId, String productNo, String sku, String productName,
                    String unit, BigDecimal quantity, BigDecimal listPrice, BigDecimal unitPrice, BigDecimal discountRate,
                    BigDecimal taxRate, BigDecimal lineAmount) { }
}
