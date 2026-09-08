package com.aicrm.catalog.application;

import com.aicrm.catalog.domain.CatalogRepository;
import com.aicrm.catalog.domain.PriceItem;
import com.aicrm.catalog.domain.PriceList;
import com.aicrm.catalog.domain.Product;
import com.aicrm.catalog.domain.ProductCategory;
import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.error.ErrorCode;
import com.aicrm.kernel.page.PageResult;
import com.aicrm.kernel.security.Actor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatalogReadService {
    private final CatalogRepository repository;

    public CatalogReadService(CatalogRepository repository) {
        this.repository = repository;
    }

    public List<ProductCategory> categories(Actor actor) {
        require(actor, "catalog:read");
        return repository.findCategories(actor.tenantId());
    }

    public PageResult<Product> products(Actor actor, long page, long size) {
        require(actor, "catalog:read");
        return repository.findProducts(actor.tenantId(), page, size);
    }

    public Product product(Actor actor, long productId) {
        require(actor, "catalog:read");
        return repository.findProduct(actor.tenantId(), productId)
                .orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "产品不存在"));
    }

    public PriceList priceList(Actor actor, long priceListId) {
        require(actor, "catalog:read");
        return repository.findPriceList(actor.tenantId(), priceListId)
                .orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "价目表不存在"));
    }

    public List<PriceItem> priceItems(Actor actor, long priceListId) {
        priceList(actor, priceListId);
        return repository.findPriceItems(actor.tenantId(), priceListId);
    }

    private void require(Actor actor, String permission) {
        if (!actor.hasPermission(permission)) {
            throw new DomainException(ErrorCode.FORBIDDEN, "无目录访问权限");
        }
    }
}
