package com.aicrm.module.product.controller;

import com.aicrm.common.PageResult;
import com.aicrm.common.Result;
import com.aicrm.common.auth.RequirePermission;
import com.aicrm.module.log.annotation.OperLog;
import com.aicrm.module.product.entity.Product;
import com.aicrm.module.product.entity.ProductCategory;
import com.aicrm.module.product.service.ProductCategoryService;
import com.aicrm.module.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 产品库接口（3.4.1）：产品分类 / 产品参数 / 附件 / 上下架
 */
@Tag(name = "产品库")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductCategoryService categoryService;

    // ---------- 分类 ----------

    @Operation(summary = "分类树列表", description = "按层级返回产品分类树，场景：产品库管理、侧边栏快捷发送选品分类；权限：product:list")
    @RequirePermission(perms = "product:list")
    @GetMapping("/product-categories")
    public Result<List<ProductCategory>> categories() {
        return Result.ok(categoryService.listTree());
    }

    @Operation(summary = "创建分类", description = "新增产品分类节点，场景：产品库分类体系维护；权限：product:edit。")
    @ApiResponse(responseCode = "400", description = "分类名称不能为空")
    @OperLog(module = "产品库", operation = "创建产品分类")
    @RequirePermission(perms = "product:edit")
    @PostMapping("/product-categories")
    public Result<ProductCategory> createCategory(@RequestBody ProductCategory category) {
        return Result.ok(categoryService.create(category));
    }

    @Operation(summary = "更新分类", description = "修改产品分类名称/父级/排序/状态，场景：产品库分类体系维护；权限：product:edit。")
    @ApiResponse(responseCode = "404", description = "产品分类不存在")
    @OperLog(module = "产品库", operation = "更新产品分类")
    @RequirePermission(perms = "product:edit")
    @PutMapping("/product-categories/{id}")
    public Result<ProductCategory> updateCategory(@Parameter(description = "分类 ID", required = true) @PathVariable Long id, @RequestBody ProductCategory category) {
        category.setId(id);
        return Result.ok(categoryService.update(category));
    }

    @Operation(summary = "删除分类", description = "删除产品分类节点（存在子分类/关联产品时按业务规则处理），场景：产品库分类体系维护；权限：product:delete。")
    @ApiResponse(responseCode = "400", description = "存在子分类/分类下存在产品，不能删除")
    @OperLog(module = "产品库", operation = "删除产品分类")
    @RequirePermission(perms = "product:delete")
    @DeleteMapping("/product-categories/{id}")
    public Result<Void> deleteCategory(@Parameter(description = "分类 ID", required = true) @PathVariable Long id) {
        categoryService.delete(id);
        return Result.ok();
    }

    @Operation(summary = "分类启停", description = "启用/停用产品分类，停用后下级不再展示，场景：产品库分类体系维护；权限：product:edit。")
    @ApiResponse(responseCode = "404", description = "产品分类不存在")
    @OperLog(module = "产品库", operation = "产品分类启停")
    @RequirePermission(perms = "product:edit")
    @PutMapping("/product-categories/{id}/status")
    public Result<Void> updateCategoryStatus(@Parameter(description = "分类 ID", required = true) @PathVariable Long id, @Parameter(description = "状态：1 启用 / 0 停用", required = true) @RequestParam Integer status) {
        categoryService.updateStatus(id, status);
        return Result.ok();
    }

    // ---------- 产品 ----------

    @Operation(summary = "产品分页查询", description = "产品列表检索，支持关键字/分类/上下架状态过滤，场景：产品库管理、侧边栏快捷发送选品；权限：product:list")
    @RequirePermission(perms = "product:list")
    @GetMapping("/products")
    public Result<PageResult<Product>> page(@Parameter(description = "关键字（产品名称/编码/规格模糊匹配）", required = false) @RequestParam(required = false) String keyword,
                                            @Parameter(description = "分类 ID", required = false) @RequestParam(required = false) Long categoryId,
                                            @Parameter(description = "上下架状态：1 上架 / 0 下架", required = false) @RequestParam(required = false) Integer status,
                                            @Parameter(description = "页码，默认 1", required = false) @RequestParam(defaultValue = "1") long page,
                                            @Parameter(description = "每页条数，默认 20", required = false) @RequestParam(defaultValue = "20") long size) {
        return Result.ok(productService.page(keyword, categoryId, status, page, size));
    }

    @Operation(summary = "产品详情", description = "按 ID 查询产品完整资料（含参数/附件），场景：产品库管理、快捷发送预览；权限：product:list")
    @RequirePermission(perms = "product:list")
    @GetMapping("/products/{id}")
    public Result<Product> detail(@Parameter(description = "产品 ID", required = true) @PathVariable Long id) {
        return Result.ok(productService.detail(id));
    }

    @Operation(summary = "创建产品", description = "新增产品资料（含参数/附件），场景：产品库管理维护；权限：product:add。")
    @ApiResponse(responseCode = "400", description = "产品名称不能为空")
    @OperLog(module = "产品库", operation = "创建产品")
    @RequirePermission(perms = "product:add")
    @PostMapping("/products")
    public Result<Product> create(@RequestBody Product product) {
        return Result.ok(productService.create(product));
    }

    @Operation(summary = "更新产品（含参数/附件）", description = "修改产品资料（含参数/附件），场景：产品库管理维护；权限：product:edit。")
    @ApiResponse(responseCode = "400", description = "产品名称不能为空")
    @ApiResponse(responseCode = "404", description = "产品不存在")
    @OperLog(module = "产品库", operation = "更新产品")
    @RequirePermission(perms = "product:edit")
    @PutMapping("/products/{id}")
    public Result<Product> update(@Parameter(description = "产品 ID", required = true) @PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        return Result.ok(productService.update(product));
    }

    @Operation(summary = "删除产品", description = "删除产品资料，场景：产品库管理维护；权限：product:delete。")
    @ApiResponse(responseCode = "404", description = "产品不存在")
    @OperLog(module = "产品库", operation = "删除产品")
    @RequirePermission(perms = "product:delete")
    @DeleteMapping("/products/{id}")
    public Result<Void> delete(@Parameter(description = "产品 ID", required = true) @PathVariable Long id) {
        productService.delete(id);
        return Result.ok();
    }

    @Operation(summary = "产品上下架", description = "上架/下架产品，下架后不可被快捷发送检索，场景：产品库上下架管理；权限：product:edit。")
    @ApiResponse(responseCode = "404", description = "产品不存在")
    @OperLog(module = "产品库", operation = "产品上下架")
    @RequirePermission(perms = "product:edit")
    @PutMapping("/products/{id}/status")
    public Result<Void> updateStatus(@Parameter(description = "产品 ID", required = true) @PathVariable Long id, @Parameter(description = "状态：1 上架 / 0 下架", required = true) @RequestParam Integer status) {
        productService.updateStatus(id, status);
        return Result.ok();
    }
}
