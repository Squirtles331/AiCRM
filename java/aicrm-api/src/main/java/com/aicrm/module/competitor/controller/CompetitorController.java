package com.aicrm.module.competitor.controller;

import com.aicrm.common.PageResult;
import com.aicrm.common.Result;
import com.aicrm.common.auth.RequirePermission;
import com.aicrm.module.competitor.entity.Competitor;
import com.aicrm.module.competitor.entity.CompetitorProduct;
import com.aicrm.module.competitor.service.CompetitorService;
import com.aicrm.module.log.annotation.OperLog;
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
 * 竞品库接口（3.4.2）：竞品主体档案 / 产品参数 / 优劣势 / 攻防话术
 */
@Tag(name = "竞品库")
@RestController
@RequestMapping("/api/competitors")
@RequiredArgsConstructor
public class CompetitorController {

    private final CompetitorService competitorService;

    @Operation(summary = "竞品分页查询", description = "竞品列表检索，支持关键字/品类过滤，场景：竞品库管理、知识检索；权限：competitor:list")
    @RequirePermission(perms = "competitor:list")
    @GetMapping
    public Result<PageResult<Competitor>> page(@Parameter(description = "关键字（竞品名称/主体描述模糊匹配）", required = false) @RequestParam(required = false) String keyword,
                                               @Parameter(description = "竞品品类", required = false) @RequestParam(required = false) String category,
                                               @Parameter(description = "页码，默认 1", required = false) @RequestParam(defaultValue = "1") long page,
                                               @Parameter(description = "每页条数，默认 20", required = false) @RequestParam(defaultValue = "20") long size) {
        return Result.ok(competitorService.page(keyword, category, page, size));
    }

    @Operation(summary = "竞品详情", description = "按 ID 查询竞品完整档案（含优劣势/攻防话术），场景：竞品库管理、知识检索；权限：competitor:list")
    @RequirePermission(perms = "competitor:list")
    @GetMapping("/{id}")
    public Result<Competitor> detail(@Parameter(description = "竞品 ID", required = true) @PathVariable Long id) {
        return Result.ok(competitorService.detail(id));
    }

    @Operation(summary = "创建竞品", description = "新增竞品主体档案，场景：竞品库管理维护；权限：competitor:add。⚠️ 异步联动：本操作会触发 RabbitMQ 通知（exchange=aicrm.events，routingKey=knowledge.sync），Python 侧消费后更新向量数据；MQ 不可用时仅记录日志降级，不影响本接口返回")
    @OperLog(module = "竞品库", operation = "创建竞品")
    @RequirePermission(perms = "competitor:add")
    @PostMapping
    public Result<Competitor> create(@RequestBody Competitor competitor) {
        return Result.ok(competitorService.create(competitor));
    }

    @Operation(summary = "更新竞品（优劣势/攻防话术）", description = "修改竞品档案（含优劣势/攻防话术），场景：竞品库管理维护；权限：competitor:edit。⚠️ 异步联动：本操作会触发 RabbitMQ 通知（exchange=aicrm.events，routingKey=knowledge.sync），Python 侧消费后更新向量数据；MQ 不可用时仅记录日志降级，不影响本接口返回")
    @ApiResponse(responseCode = "400", description = "竞品名称不能为空/竞品不存在")
    @OperLog(module = "竞品库", operation = "更新竞品")
    @RequirePermission(perms = "competitor:edit")
    @PutMapping("/{id}")
    public Result<Competitor> update(@Parameter(description = "竞品 ID", required = true) @PathVariable Long id, @RequestBody Competitor competitor) {
        competitor.setId(id);
        return Result.ok(competitorService.update(competitor));
    }

    @Operation(summary = "删除竞品", description = "删除竞品档案（含关联竞品产品），场景：竞品库管理维护；权限：competitor:delete。⚠️ 异步联动：本操作会触发 RabbitMQ 通知（exchange=aicrm.events，routingKey=knowledge.sync），Python 侧消费后更新向量数据；MQ 不可用时仅记录日志降级，不影响本接口返回")
    @ApiResponse(responseCode = "404", description = "竞品不存在")
    @OperLog(module = "竞品库", operation = "删除竞品")
    @RequirePermission(perms = "competitor:delete")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "竞品 ID", required = true) @PathVariable Long id) {
        competitorService.delete(id);
        return Result.ok();
    }

    @Operation(summary = "竞品启停", description = "启用/停用竞品，停用后不可被知识检索，场景：竞品库上下架管理；权限：competitor:edit。⚠️ 异步联动：本操作会触发 RabbitMQ 通知（exchange=aicrm.events，routingKey=knowledge.sync），Python 侧消费后更新向量数据；MQ 不可用时仅记录日志降级，不影响本接口返回")
    @ApiResponse(responseCode = "404", description = "竞品不存在")
    @OperLog(module = "竞品库", operation = "竞品启停")
    @RequirePermission(perms = "competitor:edit")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@Parameter(description = "竞品 ID", required = true) @PathVariable Long id, @Parameter(description = "状态：1 启用 / 0 停用", required = true) @RequestParam Integer status) {
        competitorService.updateStatus(id, status);
        return Result.ok();
    }

    // ---------- 竞品产品 ----------

    @Operation(summary = "竞品产品列表", description = "查询指定竞品下的产品参数列表，场景：竞品库管理、知识检索；权限：competitor:list")
    @RequirePermission(perms = "competitor:list")
    @GetMapping("/{id}/products")
    public Result<List<CompetitorProduct>> listProducts(@Parameter(description = "竞品 ID", required = true) @PathVariable Long id) {
        return Result.ok(competitorService.listProducts(id));
    }

    @Operation(summary = "新增竞品产品参数", description = "为指定竞品新增产品参数，场景：竞品库管理维护；权限：competitor:add。⚠️ 异步联动：本操作会触发 RabbitMQ 通知（exchange=aicrm.events，routingKey=knowledge.sync），Python 侧消费后更新向量数据；MQ 不可用时仅记录日志降级，不影响本接口返回")
    @OperLog(module = "竞品库", operation = "新增竞品产品")
    @RequirePermission(perms = "competitor:add")
    @PostMapping("/{id}/products")
    public Result<CompetitorProduct> createProduct(@Parameter(description = "竞品 ID", required = true) @PathVariable Long id, @RequestBody CompetitorProduct product) {
        product.setCompetitorId(id);
        return Result.ok(competitorService.createProduct(product));
    }

    @Operation(summary = "更新竞品产品参数", description = "修改竞品产品参数，场景：竞品库管理维护；权限：competitor:edit。⚠️ 异步联动：本操作会触发 RabbitMQ 通知（exchange=aicrm.events，routingKey=knowledge.sync），Python 侧消费后更新向量数据；MQ 不可用时仅记录日志降级，不影响本接口返回")
    @ApiResponse(responseCode = "400", description = "竞品产品名称不能为空")
    @ApiResponse(responseCode = "404", description = "竞品产品不存在")
    @OperLog(module = "竞品库", operation = "更新竞品产品")
    @RequirePermission(perms = "competitor:edit")
    @PutMapping("/products/{productId}")
    public Result<CompetitorProduct> updateProduct(@Parameter(description = "竞品产品 ID", required = true) @PathVariable Long productId,
                                                   @RequestBody CompetitorProduct product) {
        product.setId(productId);
        return Result.ok(competitorService.updateProduct(product));
    }

    @Operation(summary = "删除竞品产品", description = "删除竞品产品参数，场景：竞品库管理维护；权限：competitor:delete。⚠️ 异步联动：本操作会触发 RabbitMQ 通知（exchange=aicrm.events，routingKey=knowledge.sync），Python 侧消费后更新向量数据；MQ 不可用时仅记录日志降级，不影响本接口返回")
    @ApiResponse(responseCode = "404", description = "竞品产品不存在")
    @OperLog(module = "竞品库", operation = "删除竞品产品")
    @RequirePermission(perms = "competitor:delete")
    @DeleteMapping("/products/{productId}")
    public Result<Void> deleteProduct(@Parameter(description = "竞品产品 ID", required = true) @PathVariable Long productId) {
        competitorService.deleteProduct(productId);
        return Result.ok();
    }
}
