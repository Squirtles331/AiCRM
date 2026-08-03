package com.aicrm.module.user.controller;

import com.aicrm.common.PageResult;
import com.aicrm.common.Result;
import com.aicrm.common.auth.RequirePermission;
import com.aicrm.module.log.annotation.OperLog;
import com.aicrm.module.user.entity.User;
import com.aicrm.module.user.service.UserService;
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

import java.util.List;

/**
 * 坐席管理接口
 */
@Tag(name = "坐席管理")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "分页查询坐席", description = "需权限 user:list")
    @RequirePermission(perms = "user:list")
    @GetMapping
    public Result<PageResult<User>> page(
            @Parameter(description = "租户 ID") @RequestParam(required = false) Long tenantId,
            @Parameter(description = "页码，默认 1") @RequestParam(defaultValue = "1") long page,
            @Parameter(description = "每页条数，默认 20") @RequestParam(defaultValue = "20") long size) {
        return Result.ok(userService.pageUsers(tenantId, page, size));
    }

    @Operation(summary = "查询坐席详情", description = "需权限 user:list")
    @RequirePermission(perms = "user:list")
    @GetMapping("/{id}")
    public Result<User> detail(@Parameter(description = "坐席 ID", required = true) @PathVariable Long id) {
        return Result.ok(userService.getById(id));
    }

    @Operation(summary = "创建坐席", description = "需权限 user:add")
    @ApiResponse(responseCode = "400", description = "租户 ID/手机号/密码不能为空")
    @OperLog(module = "用户管理", operation = "创建用户")
    @RequirePermission(perms = "user:add")
    @PostMapping
    public Result<User> create(@Valid @RequestBody User user) {
        return Result.ok(userService.createUser(user));
    }

    @Operation(summary = "更新坐席", description = "需权限 user:edit")
    @ApiResponse(responseCode = "404", description = "用户不存在（业务码 1101）")
    @OperLog(module = "用户管理", operation = "更新用户")
    @RequirePermission(perms = "user:edit")
    @PutMapping("/{id}")
    public Result<User> update(@Parameter(description = "坐席 ID", required = true) @PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        return Result.ok(userService.updateUser(user));
    }

    @Operation(summary = "删除坐席（逻辑删除）", description = "需权限 user:delete")
    @ApiResponse(responseCode = "404", description = "用户不存在（业务码 1101）")
    @OperLog(module = "用户管理", operation = "删除用户")
    @RequirePermission(perms = "user:delete")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "坐席 ID", required = true) @PathVariable Long id) {
        userService.deleteUser(id);
        return Result.ok();
    }

    @Operation(summary = "重置密码", description = "需权限 user:reset-password")
    @ApiResponse(responseCode = "400", description = "新密码长度不能少于 6 位")
    @ApiResponse(responseCode = "404", description = "用户不存在（业务码 1101）")
    @OperLog(module = "用户管理", operation = "重置密码")
    @RequirePermission(perms = "user:reset-password")
    @PutMapping("/{id}/password")
    public Result<Void> resetPassword(@Parameter(description = "坐席 ID", required = true) @PathVariable Long id,
                                      @Parameter(description = "新密码（明文）", required = true) @RequestParam String newPassword) {
        userService.resetPassword(id, newPassword);
        return Result.ok();
    }

    @Operation(summary = "启用/停用坐席", description = "需权限 user:edit")
    @ApiResponse(responseCode = "400", description = "status 仅支持 0/1")
    @ApiResponse(responseCode = "404", description = "用户不存在（业务码 1101）")
    @OperLog(module = "用户管理", operation = "启用/停用用户")
    @RequirePermission(perms = "user:edit")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@Parameter(description = "坐席 ID", required = true) @PathVariable Long id,
                                     @Parameter(description = "状态：1启用/0停用", required = true) @RequestParam Integer status) {
        userService.updateStatus(id, status);
        return Result.ok();
    }

    @Operation(summary = "查询用户已分配角色 ID", description = "需权限 user:list")
    @RequirePermission(perms = "user:list")
    @GetMapping("/{id}/roles")
    public Result<List<Long>> roleIds(@Parameter(description = "坐席 ID", required = true) @PathVariable Long id) {
        return Result.ok(userService.getRoleIds(id));
    }

    @Operation(summary = "分配用户角色（全量覆盖）", description = "需权限 user:edit")
    @ApiResponse(responseCode = "404", description = "用户不存在（业务码 1101）")
    @OperLog(module = "用户管理", operation = "分配用户角色")
    @RequirePermission(perms = "user:edit")
    @PutMapping("/{id}/roles")
    public Result<Void> setRoles(@Parameter(description = "坐席 ID", required = true) @PathVariable Long id, @RequestBody List<Long> roleIds) {
        userService.setUserRoles(id, roleIds);
        return Result.ok();
    }
}
