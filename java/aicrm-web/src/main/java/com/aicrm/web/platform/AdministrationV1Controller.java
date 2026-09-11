package com.aicrm.web.platform;

import com.aicrm.kernel.page.PageResult;
import com.aicrm.kernel.security.ActorContext;
import com.aicrm.kernel.security.TraceContext;
import com.aicrm.platform.application.AdministrationReadService;
import com.aicrm.web.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/administration")
@Tag(name = "CRM V1 - 系统管理")
public class AdministrationV1Controller {
    private final AdministrationReadService reads;

    public AdministrationV1Controller(AdministrationReadService reads) {
        this.reads = reads;
    }

    @GetMapping("/users")
    @Operation(summary = "分页查询租户用户")
    public ApiResponse<PageResult<AdministrationReadService.UserView>> users(
            @RequestParam(defaultValue = "1") @Min(1) long page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(200) long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return success(reads.users(ActorContext.require(), page, size, keyword, status));
    }

    @GetMapping("/roles")
    @Operation(summary = "分页查询租户角色")
    public ApiResponse<PageResult<AdministrationReadService.RoleView>> roles(
            @RequestParam(defaultValue = "1") @Min(1) long page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(200) long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return success(reads.roles(ActorContext.require(), page, size, keyword, status));
    }

    @GetMapping("/departments")
    @Operation(summary = "查询租户部门")
    public ApiResponse<List<AdministrationReadService.DepartmentView>> departments() {
        return success(reads.departments(ActorContext.require()));
    }

    @GetMapping("/permissions")
    @Operation(summary = "查询租户权限")
    public ApiResponse<List<AdministrationReadService.PermissionView>> permissions() {
        return success(reads.permissions(ActorContext.require()));
    }

    private <T> ApiResponse<T> success(T data) {
        return ApiResponse.success(data, TraceContext.get());
    }
}
