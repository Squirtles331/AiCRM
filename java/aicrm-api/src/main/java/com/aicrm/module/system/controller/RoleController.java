package com.aicrm.module.system.controller;

import com.aicrm.common.PageResult;
import com.aicrm.common.Result;
import com.aicrm.common.auth.RequirePermission;
import com.aicrm.module.log.annotation.OperLog;
import com.aicrm.module.system.entity.Role;
import com.aicrm.module.system.service.RoleService;
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
 * 角色管理接口
 */
@Tag(name = "角色管理")
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "分页查询角色", description = "需权限 role:list")
    @RequirePermission(perms = "role:list")
    @GetMapping
    public Result<PageResult<Role>> page(
            @Parameter(description = "关键字（角色名称/编码）") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码，默认 1") @RequestParam(defaultValue = "1") long page,
            @Parameter(description = "每页条数，默认 20") @RequestParam(defaultValue = "20") long size) {
        return Result.ok(roleService.pageRoles(keyword, page, size));
    }

    @Operation(summary = "启用角色列表（下拉用）", description = "登录用户即可访问")
    @GetMapping("/enabled")
    public Result<List<Role>> enabled() {
        return Result.ok(roleService.listEnabled());
    }

    @Operation(summary = "角色详情", description = "需权限 role:list")
    @RequirePermission(perms = "role:list")
    @GetMapping("/{id}")
    public Result<Role> detail(@Parameter(description = "角色 ID", required = true) @PathVariable Long id) {
        return Result.ok(roleService.detail(id));
    }

    @Operation(summary = "创建角色", description = "需权限 role:add")
    @ApiResponse(responseCode = "400", description = "角色编码与名称不能为空/角色编码已存在")
    @OperLog(module = "角色管理", operation = "创建角色")
    @RequirePermission(perms = "role:add")
    @PostMapping
    public Result<Role> create(@RequestBody Role role) {
        return Result.ok(roleService.createRole(role));
    }

    @Operation(summary = "更新角色", description = "需权限 role:edit")
    @ApiResponse(responseCode = "400", description = "角色不存在")
    @OperLog(module = "角色管理", operation = "更新角色")
    @RequirePermission(perms = "role:edit")
    @PutMapping("/{id}")
    public Result<Role> update(@Parameter(description = "角色 ID", required = true) @PathVariable Long id, @RequestBody Role role) {
        role.setId(id);
        return Result.ok(roleService.updateRole(role));
    }

    @Operation(summary = "删除角色", description = "需权限 role:delete")
    @ApiResponse(responseCode = "400", description = "角色不存在")
    @OperLog(module = "角色管理", operation = "删除角色")
    @RequirePermission(perms = "role:delete")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "角色 ID", required = true) @PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.ok();
    }

    @Operation(summary = "启用/停用角色", description = "需权限 role:edit")
    @ApiResponse(responseCode = "400", description = "角色不存在/status 仅支持 0/1")
    @OperLog(module = "角色管理", operation = "启用/停用角色")
    @RequirePermission(perms = "role:edit")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@Parameter(description = "角色 ID", required = true) @PathVariable Long id,
                                     @Parameter(description = "状态：1启用/0停用", required = true) @RequestParam Integer status) {
        roleService.updateStatus(id, status);
        return Result.ok();
    }

    @Operation(summary = "查询角色已分配的菜单 ID", description = "需权限 role:assign-menu")
    @RequirePermission(perms = "role:assign-menu")
    @GetMapping("/{id}/menus")
    public Result<List<Long>> menuIds(@Parameter(description = "角色 ID", required = true) @PathVariable Long id) {
        return Result.ok(roleService.listMenuIdsByRole(id));
    }

    @Operation(summary = "分配角色菜单（全量覆盖）", description = "需权限 role:assign-menu")
    @ApiResponse(responseCode = "400", description = "角色不存在")
    @OperLog(module = "角色管理", operation = "分配角色菜单")
    @RequirePermission(perms = "role:assign-menu")
    @PutMapping("/{id}/menus")
    public Result<Void> setMenus(@Parameter(description = "角色 ID", required = true) @PathVariable Long id, @RequestBody List<Long> menuIds) {
        roleService.setRoleMenus(id, menuIds);
        return Result.ok();
    }
}
