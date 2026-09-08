package com.aicrm.catalog.application;

import com.aicrm.catalog.domain.CatalogRepository;
import com.aicrm.catalog.domain.PriceItem;
import com.aicrm.catalog.domain.PriceList;
import com.aicrm.catalog.domain.Product;
import com.aicrm.catalog.domain.ProductCategory;
import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.error.ErrorCode;
import com.aicrm.kernel.event.DomainEvent;
import com.aicrm.kernel.id.IdGenerator;
import com.aicrm.kernel.security.Actor;
import com.aicrm.platform.application.AuditLogService;
import com.aicrm.platform.application.IdempotencyService;
import com.aicrm.platform.application.OutboxService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

/** Transactional catalog commands. Prices may only be maintained while a price list is in DRAFT. */
@Service
public class CatalogCommandService {
    private final CatalogRepository repository;
    private final IdGenerator idGenerator;
    private final IdempotencyService idempotency;
    private final AuditLogService audit;
    private final OutboxService outbox;
    private final ObjectMapper objectMapper;

    public CatalogCommandService(CatalogRepository repository, IdGenerator idGenerator, IdempotencyService idempotency,
                                 AuditLogService audit, OutboxService outbox, ObjectMapper objectMapper) {
        this.repository = repository;
        this.idGenerator = idGenerator;
        this.idempotency = idempotency;
        this.audit = audit;
        this.outbox = outbox;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public ProductCategory createCategory(Actor actor, CatalogCommands.CreateCategory command, String key) {
        require(actor, "catalog:write");
        return idempotency.execute(actor, "catalog:category:create", key, command, ProductCategory.class, () -> {
            if (command.parentId() != null) {
                ProductCategory parent = category(actor, command.parentId());
                if (parent.status() != ProductCategory.Status.ACTIVE) {
                    throw new DomainException(ErrorCode.VALIDATION_ERROR, "上级分类已停用");
                }
            }
            Instant now = Instant.now();
            ProductCategory result = repository.insertCategory(new ProductCategory(idGenerator.nextId(), actor.tenantId(),
                    command.parentId(), required(command.code(), "分类编码"), required(command.name(), "分类名称"),
                    ProductCategory.Status.ACTIVE, command.sortOrder(), 0, now, now), actor.userId());
            journal(actor, "CATALOG_CATEGORY_CREATE", "PRODUCT_CATEGORY", result.id(), result, "CategoryCreated");
            return result;
        });
    }

    @Transactional
    public Product createProduct(Actor actor, CatalogCommands.CreateProduct command, String key) {
        require(actor, "catalog:write");
        return idempotency.execute(actor, "catalog:product:create", key, command, Product.class, () -> {
            if (command.categoryId() != null && category(actor, command.categoryId()).status() != ProductCategory.Status.ACTIVE) {
                throw new DomainException(ErrorCode.VALIDATION_ERROR, "产品分类已停用");
            }
            Instant now = Instant.now();
            Product result = repository.insertProduct(new Product(idGenerator.nextId(), actor.tenantId(), command.categoryId(),
                    "PRD-" + idGenerator.nextId(), required(command.sku(), "SKU"), required(command.name(), "产品名称"),
                    trim(command.specification()), required(command.unit(), "计量单位"), Product.Status.ACTIVE, true, 0, now, now), actor.userId());
            journal(actor, "CATALOG_PRODUCT_CREATE", "PRODUCT", result.id(), result, "ProductCreated");
            return result;
        });
    }

    @Transactional
    public PriceList createPriceList(Actor actor, CatalogCommands.CreatePriceList command, String key) {
        require(actor, "catalog:write");
        return idempotency.execute(actor, "catalog:price-list:create", key, command, PriceList.class, () -> {
            if (command.effectiveTo() != null && command.effectiveFrom() != null && !command.effectiveTo().isAfter(command.effectiveFrom())) {
                throw new DomainException(ErrorCode.VALIDATION_ERROR, "价目表结束时间必须晚于开始时间");
            }
            Instant now = Instant.now();
            PriceList result = repository.insertPriceList(new PriceList(idGenerator.nextId(), actor.tenantId(),
                    required(command.code(), "价目表编码"), required(command.name(), "价目表名称"), currency(command.currency()),
                    PriceList.Status.DRAFT, command.effectiveFrom(), command.effectiveTo(), 0, now, now), actor.userId());
            journal(actor, "CATALOG_PRICE_LIST_CREATE", "PRICE_LIST", result.id(), result, "PriceListCreated");
            return result;
        });
    }

    @Transactional
    public PriceItem addPriceItem(Actor actor, CatalogCommands.AddPriceItem command, String key) {
        require(actor, "catalog:write");
        return idempotency.execute(actor, "catalog:price-item:create", key, command, PriceItem.class, () -> {
            PriceList list = priceList(actor, command.priceListId());
            if (list.status() != PriceList.Status.DRAFT) {
                throw new DomainException(ErrorCode.VALIDATION_ERROR, "只有草稿价目表可维护价格项");
            }
            Product product = product(actor, command.productId());
            if (product.status() != Product.Status.ACTIVE || !product.saleEnabled()) {
                throw new DomainException(ErrorCode.VALIDATION_ERROR, "产品不可销售");
            }
            validatePrice(command.listPrice(), command.minimumPrice(), command.taxRate());
            Instant now = Instant.now();
            PriceItem result = repository.insertPriceItem(new PriceItem(idGenerator.nextId(), actor.tenantId(), list.id(),
                    product.id(), command.listPrice(), command.minimumPrice(), command.taxRate() == null ? BigDecimal.ZERO : command.taxRate(),
                    PriceItem.Status.ACTIVE, 0, now, now), actor.userId());
            journal(actor, "CATALOG_PRICE_ITEM_CREATE", "PRICE_ITEM", result.id(), result, "PriceItemCreated");
            return result;
        });
    }

    @Transactional
    public PriceList publishPriceList(Actor actor, long priceListId, long version) {
        require(actor, "catalog:publish");
        PriceList before = priceList(actor, priceListId);
        if (before.status() != PriceList.Status.DRAFT || !repository.hasActivePriceItems(actor.tenantId(), priceListId)) {
            throw new DomainException(ErrorCode.VALIDATION_ERROR, "仅包含有效价格项的草稿价目表可以发布");
        }
        if (!repository.publishPriceList(actor.tenantId(), priceListId, version, actor.userId())) {
            throw new DomainException(ErrorCode.CONFLICT, "价目表已被其他操作修改");
        }
        PriceList after = priceList(actor, priceListId);
        journal(actor, "CATALOG_PRICE_LIST_PUBLISH", "PRICE_LIST", after.id(), Map.of("before", before, "after", after), "PriceListPublished");
        return after;
    }

    private ProductCategory category(Actor actor, long id) {
        return repository.findCategory(actor.tenantId(), id).orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "产品分类不存在"));
    }
    private Product product(Actor actor, long id) {
        return repository.findProduct(actor.tenantId(), id).orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "产品不存在"));
    }
    private PriceList priceList(Actor actor, long id) {
        return repository.findPriceList(actor.tenantId(), id).orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "价目表不存在"));
    }
    private void require(Actor actor, String permission) {
        if (!actor.hasPermission(permission)) throw new DomainException(ErrorCode.FORBIDDEN, "无目录维护权限");
    }
    private void validatePrice(BigDecimal list, BigDecimal minimum, BigDecimal tax) {
        if (list == null || list.signum() < 0 || (minimum != null && (minimum.signum() < 0 || minimum.compareTo(list) > 0))
                || (tax != null && (tax.signum() < 0 || tax.compareTo(BigDecimal.ONE) > 0))) {
            throw new DomainException(ErrorCode.VALIDATION_ERROR, "价格、最低价或税率不合法");
        }
    }
    private String required(String value, String field) {
        String result = trim(value);
        if (result == null) throw new DomainException(ErrorCode.VALIDATION_ERROR, field + "不能为空");
        return result;
    }
    private String currency(String value) {
        String result = required(value, "币种").toUpperCase();
        if (!result.matches("[A-Z]{3}")) throw new DomainException(ErrorCode.VALIDATION_ERROR, "币种必须为 ISO 4217 三位代码");
        return result;
    }
    private String trim(String value) { return value == null || value.trim().isEmpty() ? null : value.trim(); }
    private void journal(Actor actor, String action, String type, long id, Object payload, String eventType) {
        String operation = action + ":" + id;
        String json = json(payload);
        audit.record(actor, action, type, id, operation, "API", null, "{}", json);
        outbox.append(new DomainEvent(eventType, type, id, actor.tenantId(), json, Instant.now()), operation, actor.userId());
    }
    private String json(Object value) {
        try { return objectMapper.writeValueAsString(value); }
        catch (JsonProcessingException exception) { throw new IllegalStateException("无法序列化目录事件", exception); }
    }
}
