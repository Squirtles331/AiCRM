package com.aicrm.module.tenant.controller;

import com.aicrm.common.PageResult;
import com.aicrm.common.Result;
import com.aicrm.common.auth.RequirePermission;
import com.aicrm.module.log.annotation.OperLog;
import com.aicrm.module.tenant.dto.TenantCreateRequest;
import com.aicrm.module.tenant.entity.Tenant;
import com.aicrm.module.tenant.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
 * 租户管理接口（平台级，需 admin 角色）
 */
@Tag(name = "租户管理")
@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @Operation(summary = "分页查询租户", description = "需权限 admin")
    @RequirePermission("admin")
    @GetMapping
    public Result<PageResult<Tenant>> page(
            @Parameter(description = "关键字（企业名称）") @RequestParam(required = false) String keyword,
            @Parameter(description = "套餐编码：starter/pro/enterprise") @RequestParam(required = false) String planCode,
            @Parameter(description = "状态：1启用/0停用") @RequestParam(required = false) Integer status,
            @Parameter(description = "页码，默认 1") @RequestParam(defaultValue = "1") long page,
            @Parameter(description = "每页条数，默认 20") @RequestParam(defaultValue = "20") long size) {
        return Result.ok(tenantService.pageTenants(keyword, planCode, status, page, size));
    }

    @Operation(summary = "租户详情", description = "需权限 admin")
    @RequirePermission("admin")
    @GetMapping("/{id}")
    public Result<Tenant> detail(@Parameter(description = "租户 ID", required = true) @PathVariable Long id) {
        return Result.ok(tenantService.detail(id));
    }

    @Operation(summary = "创建租户（自动初始化默认管理员账号）", description = "需权限 admin")
    @ApiResponse(responseCode = "400", description = "管理员手机号不能为空")
    @ApiResponse(responseCode = "1004", description = "套餐不存在")
    @ApiResponse(responseCode = "1005", description = "套餐已下架")
    @OperLog(module = "租户管理", operation = "创建租户")
    @RequirePermission("admin")
    @PostMapping
    public Result<Tenant> create(@Valid @RequestBody TenantCreateRequest request) {
        return Result.ok(tenantService.createTenant(request));
    }

    @Operation(summary = "更新租户（含套餐/到期时间/联系人等）", description = "需权限 admin")
    @ApiResponse(responseCode = "1001", description = "租户不存在")
    @OperLog(module = "租户管理", operation = "更新租户")
    @RequirePermission("admin")
    @PutMapping("/{id}")
    public Result<Tenant> update(@Parameter(description = "租户 ID", required = true) @PathVariable Long id, @RequestBody Tenant tenant) {
        tenant.setId(id);
        return Result.ok(tenantService.updateTenant(tenant));
    }

    @Operation(summary = "启用/停用租户", description = "需权限 admin")
    @ApiResponse(responseCode = "400", description = "status 仅支持 0（停用）/1（启用）")
    @ApiResponse(responseCode = "1001", description = "租户不存在")
    @OperLog(module = "租户管理", operation = "启用/停用租户")
    @RequirePermission("admin")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@Parameter(description = "租户 ID", required = true) @PathVariable Long id,
                                     @Parameter(description = "状态：1启用/0停用", required = true) @RequestParam Integer status) {
        tenantService.updateStatus(id, status);
        return Result.ok();
    }

    @Operation(summary = "删除租户（逻辑删除）", description = "需权限 admin")
    @ApiResponse(responseCode = "1001", description = "租户不存在")
    @OperLog(module = "租户管理", operation = "删除租户")
    @RequirePermission("admin")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "租户 ID", required = true) @PathVariable Long id) {
        tenantService.deleteTenant(id);
        return Result.ok();
    }
}
