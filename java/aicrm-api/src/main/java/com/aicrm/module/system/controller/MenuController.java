package com.aicrm.module.system.controller;

import com.aicrm.common.Result;
import com.aicrm.common.auth.RequirePermission;
import com.aicrm.module.log.annotation.OperLog;
import com.aicrm.module.system.entity.Menu;
import com.aicrm.module.system.service.MenuService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 菜单管理接口
 */
@Tag(name = "菜单管理")
@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @Operation(summary = "全量菜单树（含按钮，平台管理用）", description = "需权限 menu:list")
    @RequirePermission(perms = "menu:list")
    @GetMapping
    public Result<List<Menu>> tree() {
        return Result.ok(menuService.listTree());
    }

    @Operation(summary = "当前用户可见菜单树（前端路由）", description = "登录用户即可访问")
    @GetMapping("/routers")
    public Result<List<Menu>> routers() {
        return Result.ok(menuService.getRoutersByUser());
    }

    @Operation(summary = "创建菜单", description = "需权限 menu:add")
    @ApiResponse(responseCode = "400", description = "菜单名称不能为空")
    @OperLog(module = "菜单管理", operation = "创建菜单")
    @RequirePermission(perms = "menu:add")
    @PostMapping
    public Result<Menu> create(@RequestBody Menu menu) {
        return Result.ok(menuService.createMenu(menu));
    }

    @Operation(summary = "更新菜单", description = "需权限 menu:edit")
    @ApiResponse(responseCode = "400", description = "菜单不存在/父菜单不能是自己")
    @OperLog(module = "菜单管理", operation = "更新菜单")
    @RequirePermission(perms = "menu:edit")
    @PutMapping("/{id}")
    public Result<Menu> update(@Parameter(description = "菜单 ID", required = true) @PathVariable Long id, @RequestBody Menu menu) {
        menu.setId(id);
        return Result.ok(menuService.updateMenu(menu));
    }

    @Operation(summary = "删除菜单", description = "需权限 menu:delete")
    @ApiResponse(responseCode = "400", description = "菜单不存在/存在子菜单，请先删除子菜单")
    @OperLog(module = "菜单管理", operation = "删除菜单")
    @RequirePermission(perms = "menu:delete")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "菜单 ID", required = true) @PathVariable Long id) {
        menuService.deleteMenu(id);
        return Result.ok();
    }
}
