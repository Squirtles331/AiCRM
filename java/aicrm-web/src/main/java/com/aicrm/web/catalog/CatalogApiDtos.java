package com.aicrm.web.catalog;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

final class CatalogApiDtos {
    private CatalogApiDtos() { }

    record CreateCategoryRequest(Long parentId, @NotBlank String code, @NotBlank String name,
                                 @Min(0) int sortOrder) { }
    record CreateProductRequest(Long categoryId, @NotBlank String sku, @NotBlank String name,
                                String specification, @NotBlank String unit) { }
    record CreatePriceListRequest(@NotBlank String code, @NotBlank String name, @NotBlank String currency,
                                  Instant effectiveFrom, Instant effectiveTo) { }
    record CreatePriceItemRequest(@NotNull Long productId, @NotNull @DecimalMin("0.00") BigDecimal listPrice,
                                  @DecimalMin("0.00") BigDecimal minimumPrice,
                                  @DecimalMin("0.00") @Max(1) BigDecimal taxRate) { }
    record PublishRequest(@NotNull Long version) { }

    record CategoryView(String id, String parentId, String code, String name, String status, int sortOrder, long version) { }
    record ProductView(String id, String categoryId, String productNo, String sku, String name, String specification,
                       String unit, String status, boolean saleEnabled, long version) { }
    record PriceListView(String id, String code, String name, String currency, String status, Instant effectiveFrom,
                         Instant effectiveTo, long version) { }
    record PriceItemView(String id, String priceListId, String productId, BigDecimal listPrice, BigDecimal minimumPrice,
                         BigDecimal taxRate, String status, long version) { }
    record PageView<T>(List<T> items, long page, long size, long total) { }
}
