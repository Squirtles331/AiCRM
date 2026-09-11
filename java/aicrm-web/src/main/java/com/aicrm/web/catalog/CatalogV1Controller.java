package com.aicrm.web.catalog;

import com.aicrm.catalog.application.CatalogCommandService;
import com.aicrm.catalog.application.CatalogCommands;
import com.aicrm.catalog.application.CatalogReadService;
import com.aicrm.catalog.domain.PriceItem;
import com.aicrm.catalog.domain.PriceList;
import com.aicrm.catalog.domain.Product;
import com.aicrm.catalog.domain.ProductCategory;
import com.aicrm.kernel.page.PageResult;
import com.aicrm.kernel.security.Actor;
import com.aicrm.kernel.security.ActorContext;
import com.aicrm.kernel.security.TraceContext;
import com.aicrm.web.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/catalog")
@Tag(name = "CRM V1 - 产品与价格")
public class CatalogV1Controller {
    private final CatalogCommandService commands;
    private final CatalogReadService reads;

    public CatalogV1Controller(CatalogCommandService commands, CatalogReadService reads) {
        this.commands = commands;
        this.reads = reads;
    }

    @GetMapping("/categories")
    @Operation(summary = "查询产品分类")
    public ApiResponse<List<CatalogApiDtos.CategoryView>> categories() {
        return success(reads.categories(actor()).stream().map(CatalogV1Controller::view).toList());
    }

    @PostMapping("/categories")
    @Operation(summary = "创建产品分类")
    public ResponseEntity<ApiResponse<CatalogApiDtos.CategoryView>> createCategory(
            @Valid @RequestBody CatalogApiDtos.CreateCategoryRequest request,
            @RequestHeader("Idempotency-Key") String key) {
        ProductCategory category = commands.createCategory(actor(), new CatalogCommands.CreateCategory(request.parentId(),
                request.code(), request.name(), request.sortOrder()), key);
        return ResponseEntity.status(HttpStatus.CREATED).body(success(view(category)));
    }

    @GetMapping("/products")
    @Operation(summary = "查询产品")
    public ApiResponse<CatalogApiDtos.PageView<CatalogApiDtos.ProductView>> products(
            @RequestParam(defaultValue = "1") @Min(1) long page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(200) long size) {
        PageResult<Product> result = reads.products(actor(), page, size);
        return success(new CatalogApiDtos.PageView<>(result.records().stream().map(CatalogV1Controller::view).toList(),
                result.page(), result.size(), result.total()));
    }

    @PostMapping("/products")
    @Operation(summary = "创建产品")
    public ResponseEntity<ApiResponse<CatalogApiDtos.ProductView>> createProduct(
            @Valid @RequestBody CatalogApiDtos.CreateProductRequest request,
            @RequestHeader("Idempotency-Key") String key) {
        Product product = commands.createProduct(actor(), new CatalogCommands.CreateProduct(request.categoryId(), request.sku(),
                request.name(), request.specification(), request.unit()), key);
        return ResponseEntity.status(HttpStatus.CREATED).body(success(view(product)));
    }

    @PostMapping("/price-lists")
    @Operation(summary = "创建草稿价目表")
    public ResponseEntity<ApiResponse<CatalogApiDtos.PriceListView>> createPriceList(
            @Valid @RequestBody CatalogApiDtos.CreatePriceListRequest request,
            @RequestHeader("Idempotency-Key") String key) {
        PriceList list = commands.createPriceList(actor(), new CatalogCommands.CreatePriceList(request.code(), request.name(),
                request.currency(), request.effectiveFrom(), request.effectiveTo()), key);
        return ResponseEntity.status(HttpStatus.CREATED).body(success(view(list)));
    }

    @GetMapping("/price-lists")
    @Operation(summary = "分页查询价目表")
    public ApiResponse<CatalogApiDtos.PageView<CatalogApiDtos.PriceListView>> priceLists(
            @RequestParam(defaultValue = "1") @Min(1) long page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(200) long size) {
        PageResult<PriceList> result = reads.priceLists(actor(), page, size);
        return success(new CatalogApiDtos.PageView<>(result.records().stream().map(CatalogV1Controller::view).toList(),
                result.page(), result.size(), result.total()));
    }

    @GetMapping("/price-lists/{id}")
    @Operation(summary = "查询价目表")
    public ApiResponse<CatalogApiDtos.PriceListView> priceList(@PathVariable long id) {
        return success(view(reads.priceList(actor(), id)));
    }

    @GetMapping("/price-lists/{id}/items")
    @Operation(summary = "查询价目表价格项")
    public ApiResponse<List<CatalogApiDtos.PriceItemView>> priceItems(@PathVariable long id) {
        return success(reads.priceItems(actor(), id).stream().map(CatalogV1Controller::view).toList());
    }

    @PostMapping("/price-lists/{id}/items")
    @Operation(summary = "添加草稿价目表价格项")
    public ResponseEntity<ApiResponse<CatalogApiDtos.PriceItemView>> addPriceItem(@PathVariable long id,
            @Valid @RequestBody CatalogApiDtos.CreatePriceItemRequest request,
            @RequestHeader("Idempotency-Key") String key) {
        PriceItem item = commands.addPriceItem(actor(), new CatalogCommands.AddPriceItem(id, request.productId(), request.listPrice(),
                request.minimumPrice(), request.taxRate()), key);
        return ResponseEntity.status(HttpStatus.CREATED).body(success(view(item)));
    }

    @PostMapping("/price-lists/{id}/actions/publish")
    @Operation(summary = "发布价目表")
    public ApiResponse<CatalogApiDtos.PriceListView> publish(@PathVariable long id,
            @Valid @RequestBody CatalogApiDtos.PublishRequest request) {
        return success(view(commands.publishPriceList(actor(), id, request.version())));
    }

    private static CatalogApiDtos.CategoryView view(ProductCategory value) {
        return new CatalogApiDtos.CategoryView(id(value.id()), id(value.parentId()), value.code(), value.name(), value.status().name(), value.sortOrder(), value.version());
    }
    private static CatalogApiDtos.ProductView view(Product value) {
        return new CatalogApiDtos.ProductView(id(value.id()), id(value.categoryId()), value.productNo(), value.sku(), value.name(),
                value.specification(), value.unit(), value.status().name(), value.saleEnabled(), value.version());
    }
    private static CatalogApiDtos.PriceListView view(PriceList value) {
        return new CatalogApiDtos.PriceListView(id(value.id()), value.code(), value.name(), value.currency(), value.status().name(),
                value.effectiveFrom(), value.effectiveTo(), value.version());
    }
    private static CatalogApiDtos.PriceItemView view(PriceItem value) {
        return new CatalogApiDtos.PriceItemView(id(value.id()), id(value.priceListId()), id(value.productId()), value.listPrice(),
                value.minimumPrice(), value.taxRate(), value.status().name(), value.version());
    }
    private static String id(Long value) { return value == null ? null : String.valueOf(value); }
    private static String id(long value) { return String.valueOf(value); }
    private Actor actor() { return ActorContext.require(); }
    private <T> ApiResponse<T> success(T data) { return ApiResponse.success(data, TraceContext.get()); }
}
