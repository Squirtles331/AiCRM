package com.aicrm.module.customer.controller;

import com.aicrm.common.PageResult;
import com.aicrm.common.Result;
import com.aicrm.common.auth.RequirePermission;
import com.aicrm.module.customer.entity.Customer;
import com.aicrm.module.customer.service.CustomerService;
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

/**
 * 客户主数据管理接口（3.2.1）
 */
@Tag(name = "客户管理")
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @Operation(summary = "客户分页查询",
            description = "权限：customer:list。按关键字/行业/地区/客户阶段分页查询客户；stage 取值：new潜在/potential有意向/intention报价/negotiating谈判/won成交/lost流失，结果按综合评分倒序。")
    @RequirePermission(perms = "customer:list")
    @GetMapping
    public Result<PageResult<Customer>> page(
            @Parameter(description = "关键字（公司名称模糊匹配）") @RequestParam(required = false) String keyword,
            @Parameter(description = "行业（模糊匹配）") @RequestParam(required = false) String industry,
            @Parameter(description = "地区（模糊匹配）") @RequestParam(required = false) String region,
            @Parameter(description = "客户阶段：new潜在/potential有意向/intention报价/negotiating谈判/won成交/lost流失") @RequestParam(required = false) String stage,
            @Parameter(description = "页码，默认 1") @RequestParam(defaultValue = "1") long page,
            @Parameter(description = "每页条数，默认 20") @RequestParam(defaultValue = "20") long size) {
        return Result.ok(customerService.page(keyword, industry, region, stage, page, size));
    }

    @Operation(summary = "客户详情",
            description = "权限：customer:list。按 ID 查询客户详情（含人员架构 org_structure）。")
    @RequirePermission(perms = "customer:list")
    @GetMapping("/{id}")
    public Result<Customer> detail(@Parameter(description = "客户 ID", required = true) @PathVariable Long id) {
        return Result.ok(customerService.detail(id));
    }

    @Operation(summary = "创建客户",
            description = "权限：customer:add。新建客户公司，客户名称必填；stage 默认 new潜在。")
    @ApiResponse(responseCode = "400", description = "客户名称不能为空")
    @OperLog(module = "客户管理", operation = "创建客户")
    @RequirePermission(perms = "customer:add")
    @PostMapping
    public Result<Customer> create(@Parameter(description = "客户信息（name 必填）", required = true) @RequestBody Customer customer) {
        return Result.ok(customerService.create(customer));
    }

    @Operation(summary = "更新客户",
            description = "权限：customer:edit。按 ID 更新客户基本信息（名称/行业/规模/地区等）。")
    @ApiResponse(responseCode = "404", description = "客户不存在")
    @OperLog(module = "客户管理", operation = "更新客户")
    @RequirePermission(perms = "customer:edit")
    @PutMapping("/{id}")
    public Result<Customer> update(@Parameter(description = "客户 ID", required = true) @PathVariable Long id,
                                   @Parameter(description = "客户信息", required = true) @RequestBody Customer customer) {
        customer.setId(id);
        return Result.ok(customerService.update(customer));
    }

    @Operation(summary = "删除客户",
            description = "权限：customer:delete。按 ID 逻辑删除客户。")
    @ApiResponse(responseCode = "404", description = "客户不存在")
    @OperLog(module = "客户管理", operation = "删除客户")
    @RequirePermission(perms = "customer:delete")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "客户 ID", required = true) @PathVariable Long id) {
        customerService.delete(id);
        return Result.ok();
    }

    @Operation(summary = "客户阶段流转",
            description = "权限：customer:edit。推进/回退客户阶段；stage 取值：new潜在/potential有意向/intention报价/negotiating谈判/won成交/lost流失。")
    @ApiResponse(responseCode = "400", description = "非法客户阶段")
    @ApiResponse(responseCode = "404", description = "客户不存在")
    @OperLog(module = "客户管理", operation = "客户阶段流转")
    @RequirePermission(perms = "customer:edit")
    @PutMapping("/{id}/stage")
    public Result<Customer> updateStage(@Parameter(description = "客户 ID", required = true) @PathVariable Long id,
                                        @Parameter(description = "客户阶段：new潜在/potential有意向/intention报价/negotiating谈判/won成交/lost流失", required = true) @RequestParam String stage) {
        return Result.ok(customerService.updateStage(id, stage));
    }

    @Operation(summary = "意向等级/评分维护",
            description = "权限：customer:edit。维护客户意向等级与综合评分；intentLevel 取值 0-5，score 取值 0-100。")
    @ApiResponse(responseCode = "400", description = "意向等级需在 0-5 之间/评分需在 0-100 之间")
    @OperLog(module = "客户管理", operation = "维护意向评分")
    @RequirePermission(perms = "customer:edit")
    @PutMapping("/{id}/score")
    public Result<Customer> updateScore(@Parameter(description = "客户 ID", required = true) @PathVariable Long id,
                                        @Parameter(description = "意向等级：0-5") @RequestParam(required = false) Integer intentLevel,
                                        @Parameter(description = "综合评分：0-100") @RequestParam(required = false) Integer score) {
        return Result.ok(customerService.updateScore(id, intentLevel, score));
    }

    @Operation(summary = "企业信息回填（第三方查询）",
            description = "权限：customer:edit。第三方企业信息查询接口，有调用频率限制，请勿高频调用；返回字段以第三方响应为准。"
                    + "请求体为人员架构 org_structure 原始 JSON 字符串（可空），例如 {\"executives\":[{\"name\":\"张三\",\"title\":\"CEO\",\"phone\":\"13800138000\",\"email\":\"zhangsan@corp.com\"}],\"departments\":[\"销售部\",\"技术部\"]}；"
                    + "接口会将 org_structure 与 source 写入客户并置 enrichment_status=1，返回回填后的客户详情（含 org_structure）。")
    @ApiResponse(responseCode = "404", description = "客户不存在")
    @OperLog(module = "客户管理", operation = "企业信息回填")
    @RequirePermission(perms = "customer:edit")
    @PostMapping("/{id}/enrich")
    public Result<Customer> enrich(@Parameter(description = "客户 ID", required = true) @PathVariable Long id,
                                   @Parameter(description = "人员架构 JSON 字符串（组织成员/高管/联系方式等，结构以第三方返回为准）") @RequestBody(required = false) String orgStructure,
                                   @Parameter(description = "数据来源标识（如 public_data公开数据/customer_provided客户提供）") @RequestParam(required = false) String source) {
        return Result.ok(customerService.enrich(id, orgStructure, source));
    }
}
