package com.aicrm.module.system.controller;

import com.aicrm.common.PageResult;
import com.aicrm.common.Result;
import com.aicrm.common.auth.RequirePermission;
import com.aicrm.module.log.annotation.OperLog;
import com.aicrm.module.system.entity.Config;
import com.aicrm.module.system.service.ConfigService;
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
 * 系统参数配置接口
 */
@Tag(name = "系统参数")
@RestController
@RequestMapping("/api/configs")
@RequiredArgsConstructor
public class ConfigController {

    private final ConfigService configService;

    @Operation(summary = "参数分页", description = "需权限 config:list")
    @RequirePermission(perms = "config:list")
    @GetMapping
    public Result<PageResult<Config>> page(
            @Parameter(description = "关键字（参数名称/参数键模糊匹配）") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码，默认 1") @RequestParam(defaultValue = "1") long page,
            @Parameter(description = "每页条数，默认 20") @RequestParam(defaultValue = "20") long size) {
        return Result.ok(configService.pageConfigs(keyword, page, size));
    }

    @Operation(summary = "按 key 查询参数（业务方调用）", description = "业务方直接调用，无需登录权限，不存在时返回 null")
    @GetMapping("/key/{key}")
    public Result<Config> getByKey(
            @Parameter(description = "参数键", required = true, example = "system.siteName") @PathVariable String key) {
        return Result.ok(configService.getByKey(key));
    }

    @Operation(summary = "创建参数", description = "需权限 config:edit")
    @OperLog(module = "系统参数", operation = "创建参数")
    @RequirePermission(perms = "config:edit")
    @PostMapping
    public Result<Config> create(@RequestBody Config config) {
        return Result.ok(configService.createConfig(config));
    }

    @Operation(summary = "更新参数", description = "需权限 config:edit")
    @OperLog(module = "系统参数", operation = "更新参数")
    @RequirePermission(perms = "config:edit")
    @PutMapping("/{id}")
    public Result<Config> update(
            @Parameter(description = "参数 ID", required = true) @PathVariable Long id,
            @RequestBody Config config) {
        config.setId(id);
        return Result.ok(configService.updateConfig(config));
    }

    @Operation(summary = "删除参数（内置参数不可删除）", description = "需权限 config:edit，内置参数（configType=1）删除将返回业务错误")
    @ApiResponse(responseCode = "400", description = "内置参数不允许删除")
    @OperLog(module = "系统参数", operation = "删除参数")
    @RequirePermission(perms = "config:edit")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "参数 ID", required = true) @PathVariable Long id) {
        configService.deleteConfig(id);
        return Result.ok();
    }
}
