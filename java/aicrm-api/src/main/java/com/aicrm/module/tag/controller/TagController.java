package com.aicrm.module.tag.controller;

import com.aicrm.common.PageResult;
import com.aicrm.common.Result;
import com.aicrm.common.auth.RequirePermission;
import com.aicrm.module.customer.entity.Customer;
import com.aicrm.module.tag.entity.CustomerTag;
import com.aicrm.module.tag.entity.CustomerTagRule;
import com.aicrm.module.tag.service.CustomerTagService;
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
 * 客户标签接口（3.2.3）：标签管理、手动打标、自动规则、标签筛选
 */
@Tag(name = "客户标签")
@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final CustomerTagService customerTagService;

    // ---------- 标签管理 ----------

    @Operation(summary = "标签分页查询",
            description = "权限：tag:list。按关键字（标签名称模糊匹配）分页查询标签。")
    @RequirePermission(perms = "tag:list")
    @GetMapping
    public Result<PageResult<CustomerTag>> pageTags(@Parameter(description = "关键字（标签名称模糊匹配）") @RequestParam(required = false) String keyword,
                                                    @Parameter(description = "页码，默认 1") @RequestParam(defaultValue = "1") long page,
                                                    @Parameter(description = "每页条数，默认 20") @RequestParam(defaultValue = "20") long size) {
        return Result.ok(customerTagService.pageTags(keyword, page, size));
    }

    @Operation(summary = "创建标签",
            description = "权限：tag:add。新建客户标签定义，status 默认 1启用。")
    @ApiResponse(responseCode = "400", description = "标签名称不能为空")
    @OperLog(module = "客户标签", operation = "创建标签")
    @RequirePermission(perms = "tag:add")
    @PostMapping
    public Result<CustomerTag> createTag(@Parameter(description = "标签信息", required = true) @RequestBody CustomerTag tag) {
        return Result.ok(customerTagService.createTag(tag));
    }

    @Operation(summary = "更新标签",
            description = "权限：tag:edit。按 ID 更新标签名称/颜色/备注。")
    @ApiResponse(responseCode = "404", description = "标签不存在")
    @OperLog(module = "客户标签", operation = "更新标签")
    @RequirePermission(perms = "tag:edit")
    @PutMapping("/{id}")
    public Result<CustomerTag> updateTag(@Parameter(description = "标签 ID", required = true) @PathVariable Long id,
                                         @Parameter(description = "标签信息", required = true) @RequestBody CustomerTag tag) {
        tag.setId(id);
        return Result.ok(customerTagService.updateTag(tag));
    }

    @Operation(summary = "删除标签",
            description = "权限：tag:delete。按 ID 删除标签（含客户关联关系一并解除）。")
    @ApiResponse(responseCode = "404", description = "标签不存在")
    @OperLog(module = "客户标签", operation = "删除标签")
    @RequirePermission(perms = "tag:delete")
    @DeleteMapping("/{id}")
    public Result<Void> deleteTag(@Parameter(description = "标签 ID", required = true) @PathVariable Long id) {
        customerTagService.deleteTag(id);
        return Result.ok();
    }

    @Operation(summary = "标签启停",
            description = "权限：tag:edit。启停标签；status 取值：1启用/0停用。")
    @ApiResponse(responseCode = "400", description = "status 仅支持 0/1")
    @OperLog(module = "客户标签", operation = "标签启停")
    @RequirePermission(perms = "tag:edit")
    @PutMapping("/{id}/status")
    public Result<Void> updateTagStatus(@Parameter(description = "标签 ID", required = true) @PathVariable Long id,
                                        @Parameter(description = "状态：1启用/0停用", required = true) @RequestParam Integer status) {
        customerTagService.updateTagStatus(id, status);
        return Result.ok();
    }

    // ---------- 手动打标 / 筛选 ----------

    @Operation(summary = "给客户批量打标",
            description = "权限：tag:edit。请求体为标签 ID 列表，将多个标签绑定到指定客户。")
    @ApiResponse(responseCode = "400", description = "标签列表不能为空")
    @ApiResponse(responseCode = "404", description = "客户不存在")
    @OperLog(module = "客户标签", operation = "手动打标")
    @RequirePermission(perms = "tag:edit")
    @PostMapping("/customers/{customerId}")
    public Result<Void> tagCustomers(@Parameter(description = "客户 ID", required = true) @PathVariable Long customerId,
                                     @Parameter(description = "标签 ID 列表（如 [1,2,3]）", required = true) @RequestBody List<Long> tagIds) {
        customerTagService.tagCustomers(customerId, tagIds);
        return Result.ok();
    }

    @Operation(summary = "移除客户标签",
            description = "权限：tag:edit。解除指定客户上的单个标签绑定。")
    @ApiResponse(responseCode = "404", description = "客户/标签不存在")
    @OperLog(module = "客户标签", operation = "移除标签")
    @RequirePermission(perms = "tag:edit")
    @DeleteMapping("/customers/{customerId}/{tagId}")
    public Result<Void> untagCustomer(@Parameter(description = "客户 ID", required = true) @PathVariable Long customerId,
                                      @Parameter(description = "标签 ID", required = true) @PathVariable Long tagId) {
        customerTagService.untagCustomer(customerId, tagId);
        return Result.ok();
    }

    @Operation(summary = "标签筛选：查询某标签下的客户",
            description = "权限：tag:list。按标签 ID 分页查询打上该标签的客户列表。")
    @RequirePermission(perms = "tag:list")
    @GetMapping("/{id}/customers")
    public Result<PageResult<Customer>> pageCustomersByTag(@Parameter(description = "标签 ID", required = true) @PathVariable Long id,
                                                           @Parameter(description = "页码，默认 1") @RequestParam(defaultValue = "1") long page,
                                                           @Parameter(description = "每页条数，默认 20") @RequestParam(defaultValue = "20") long size) {
        return Result.ok(customerTagService.pageCustomersByTag(id, page, size));
    }

    // ---------- 自动标签规则 ----------

    @Operation(summary = "规则分页查询",
            description = "权限：tag:rule。分页查询自动标签规则。")
    @RequirePermission(perms = "tag:rule")
    @GetMapping("/rules")
    public Result<PageResult<CustomerTagRule>> pageRules(@Parameter(description = "页码，默认 1") @RequestParam(defaultValue = "1") long page,
                                                         @Parameter(description = "每页条数，默认 20") @RequestParam(defaultValue = "20") long size) {
        return Result.ok(customerTagService.pageRules(page, size));
    }

    @Operation(summary = "创建自动标签规则",
            description = "权限：tag:rule。新建自动标签规则；condition_field 取值：score评分/intent_level意向等级/stage客户阶段/industry行业/region地区/source来源；condition_op 取值：gt大于/gte大于等于/lt小于/lte小于等于/eq等于/contains包含。")
    @ApiResponse(responseCode = "400", description = "规则名称/规则字段/条件值非法")
    @OperLog(module = "客户标签", operation = "创建标签规则")
    @RequirePermission(perms = "tag:rule")
    @PostMapping("/rules")
    public Result<CustomerTagRule> createRule(@Parameter(description = "自动标签规则信息", required = true) @RequestBody CustomerTagRule rule) {
        return Result.ok(customerTagService.createRule(rule));
    }

    @Operation(summary = "更新自动标签规则",
            description = "权限：tag:rule。按 ID 更新自动标签规则；condition_field/condition_op 取值见创建接口。")
    @ApiResponse(responseCode = "400", description = "规则名称/条件值/规则字段非法")
    @ApiResponse(responseCode = "404", description = "标签规则不存在")
    @OperLog(module = "客户标签", operation = "更新标签规则")
    @RequirePermission(perms = "tag:rule")
    @PutMapping("/rules/{id}")
    public Result<CustomerTagRule> updateRule(@Parameter(description = "规则 ID", required = true) @PathVariable Long id,
                                              @Parameter(description = "自动标签规则信息", required = true) @RequestBody CustomerTagRule rule) {
        rule.setId(id);
        return Result.ok(customerTagService.updateRule(rule));
    }

    @Operation(summary = "删除自动标签规则",
            description = "权限：tag:rule。按 ID 删除自动标签规则。")
    @ApiResponse(responseCode = "404", description = "标签规则不存在")
    @OperLog(module = "客户标签", operation = "删除标签规则")
    @RequirePermission(perms = "tag:rule")
    @DeleteMapping("/rules/{id}")
    public Result<Void> deleteRule(@Parameter(description = "规则 ID", required = true) @PathVariable Long id) {
        customerTagService.deleteRule(id);
        return Result.ok();
    }

    @Operation(summary = "手动执行规则",
            description = "权限：tag:rule。立即对全量客户执行一次指定规则，命中客户自动打上规则绑定的标签；返回本次命中客户数。")
    @ApiResponse(responseCode = "404", description = "标签规则不存在")
    @OperLog(module = "客户标签", operation = "执行标签规则")
    @RequirePermission(perms = "tag:rule")
    @PostMapping("/rules/{id}/apply")
    public Result<Integer> applyRule(@Parameter(description = "规则 ID", required = true) @PathVariable Long id) {
        return Result.ok(customerTagService.applyRule(id));
    }
}
