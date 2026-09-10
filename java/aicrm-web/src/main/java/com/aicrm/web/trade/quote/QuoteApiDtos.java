package com.aicrm.web.trade.quote;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

final class QuoteApiDtos {
    private QuoteApiDtos() { }
    record Line(@NotNull Long productId, @NotNull Long priceItemId, @NotNull @DecimalMin("0.0001") BigDecimal quantity,
                @NotNull @DecimalMin("0.00") BigDecimal unitPrice, BigDecimal discountRate) { }
    record CreateRequest(@NotNull Long opportunityId, @NotNull Long priceListId, LocalDate validUntil,
                         @NotEmpty List<@Valid Line> lines) { }
    record VersionRequest(@NotNull Long rootVersion, @NotNull Long version) { }
    record RejectRequest(@NotNull Long rootVersion, @NotNull Long version, @NotBlank String reason) { }
    record QuoteView(String id, String quoteNo, String opportunityId, String customerId, String priceListId, String currency,
                     String status, int currentVersionNo, LocalDate validUntil, long version, Instant createdAt, Instant updatedAt) { }
    record VersionView(String id, String quoteId, int versionNo, String status, BigDecimal subtotal, BigDecimal discountAmount,
                       BigDecimal taxAmount, BigDecimal totalAmount, BigDecimal discountRate, String rejectionReason,
                       Instant submittedAt, Instant rejectedAt, Instant expiredAt, long version) { }
    record LineView(String id, String quoteVersionId, int lineNo, String productId, String priceItemId, String productNo,
                    String sku, String productName, String unit, BigDecimal quantity, BigDecimal listPrice,
                    BigDecimal minimumPrice, BigDecimal unitPrice, BigDecimal discountRate, BigDecimal taxRate,
                    BigDecimal lineAmount) { }
}
