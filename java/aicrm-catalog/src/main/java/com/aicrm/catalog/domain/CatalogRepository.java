package com.aicrm.catalog.domain;

import com.aicrm.kernel.page.PageResult;

import java.util.List;
import java.util.Optional;

/** Persistence port for the catalog aggregate roots. */
public interface CatalogRepository {
    ProductCategory insertCategory(ProductCategory category, long actorId);
    Product insertProduct(Product product, long actorId);
    PriceList insertPriceList(PriceList priceList, long actorId);
    PriceItem insertPriceItem(PriceItem priceItem, long actorId);
    Optional<ProductCategory> findCategory(long tenantId, long categoryId);
    Optional<Product> findProduct(long tenantId, long productId);
    Optional<PriceList> findPriceList(long tenantId, long priceListId);
    Optional<PriceItem> findPriceItem(long tenantId, long priceItemId);
    PageResult<Product> findProducts(long tenantId, long page, long size);
    PageResult<PriceList> findPriceLists(long tenantId, long page, long size);
    List<ProductCategory> findCategories(long tenantId);
    List<PriceItem> findPriceItems(long tenantId, long priceListId);
    boolean hasActivePriceItems(long tenantId, long priceListId);
    boolean publishPriceList(long tenantId, long priceListId, long expectedVersion, long actorId);
}
