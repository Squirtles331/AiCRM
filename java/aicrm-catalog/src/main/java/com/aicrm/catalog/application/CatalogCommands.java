package com.aicrm.catalog.application;

import java.math.BigDecimal;
import java.time.Instant;

public final class CatalogCommands {
    private CatalogCommands() { }

    public record CreateCategory(Long parentId, String code, String name, int sortOrder) { }
    public record CreateProduct(Long categoryId, String sku, String name, String specification, String unit) { }
    public record CreatePriceList(String code, String name, String currency, Instant effectiveFrom, Instant effectiveTo) { }
    public record AddPriceItem(long priceListId, long productId, BigDecimal listPrice,
                               BigDecimal minimumPrice, BigDecimal taxRate) { }
}
