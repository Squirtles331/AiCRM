package com.aicrm.module.channel.controller;

import com.aicrm.common.PageResult;
import com.aicrm.common.Result;
import com.aicrm.common.auth.RequirePermission;
import com.aicrm.module.channel.entity.ChannelAccount;
import com.aicrm.module.channel.service.ChannelAccountService;
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
 * 渠道账号管理接口：绑定、授权配置、健康状态监控、token 刷新
 */
@Tag(name = "渠道账号")
@RestController
@RequestMapping("/api/channel/accounts")
@RequiredArgsConstructor
public class ChannelAccountController {

    private final ChannelAccountService channelAccountService;

    @Operation(summary = "账号分页查询",
            description = "渠道账号分页查询，支持按账号名称关键字/渠道/健康状态过滤。需权限 channel:list。")
    @RequirePermission(perms = "channel:list")
    @GetMapping
    public Result<PageResult<ChannelAccount>> page(
            @Parameter(description = "账号名称关键字（模糊匹配）") @RequestParam(required = false) String keyword,
            @Parameter(description = "渠道 ID（对应 channel.id）") @RequestParam(required = false) Long channelId,
            @Parameter(description = "健康状态：1正常/2受限/3封禁") @RequestParam(required = false) Integer healthStatus,
            @Parameter(description = "页码，默认 1") @RequestParam(defaultValue = "1") long page,
            @Parameter(description = "每页条数，默认 20") @RequestParam(defaultValue = "20") long size) {
        return Result.ok(channelAccountService.page(keyword, channelId, healthStatus, page, size));
    }

    @Operation(summary = "绑定渠道账号",
            description = "绑定渠道账号：录入账号基础信息与授权配置 auth_config，绑定后可用于该渠道的消息收发与事件接收。需权限 channel:add。\n" +
                    "auth_config（JSONB 字符串）存储结构：{\"accessToken\":\"访问令牌\",\"refreshToken\":\"刷新令牌\",\"expireAt\":\"过期时间epoch毫秒\"}，抖音另含 appId/secret，企业微信另含 corpId/corpSecret，按渠道可扩展。\n\n" +
                    "请求示例：\n" +
                    "```json\n" +
                    "{\n" +
                    "  \"channelId\": 1,\n" +
                    "  \"accountName\": \"品牌官方号\",\n" +
                    "  \"externalId\": \"douyin_open_id_123\",\n" +
                    "  \"authConfig\": \"{\\\"accessToken\\\":\\\"at_xxx\\\",\\\"refreshToken\\\":\\\"rt_xxx\\\",\\\"expireAt\\\":\\\"1780000000000\\\",\\\"appId\\\":\\\"appid\\\",\\\"secret\\\":\\\"secret\\\"}\",\n" +
                    "  \"healthStatus\": 1,\n" +
                    "  \"riskLevel\": 0\n" +
                    "}\n" +
                    "```\n" +
                    "返回示例：\n" +
                    "```json\n" +
                    "{\n" +
                    "  \"code\": 200,\n" +
                    "  \"message\": \"操作成功\",\n" +
                    "  \"data\": {\n" +
                    "    \"id\": 1,\n" +
                    "    \"tenantId\": 1,\n" +
                    "    \"channelId\": 1,\n" +
                    "    \"accountName\": \"品牌官方号\",\n" +
                    "    \"externalId\": \"douyin_open_id_123\",\n" +
                    "    \"authConfig\": \"{\\\"accessToken\\\":\\\"at_xxx\\\",...}\",\n" +
                    "    \"healthStatus\": 1,\n" +
                    "    \"riskLevel\": 0,\n" +
                    "    \"createdAt\": \"2026-08-03 12:00:00\",\n" +
                    "    \"updatedAt\": \"2026-08-03 12:00:00\"\n" +
                    "  }\n" +
                    "}\n" +
                    "```")
    @ApiResponse(responseCode = "400", description = "账号名称/渠道不能为空")
    @OperLog(module = "渠道管理", operation = "绑定渠道账号")
    @RequirePermission(perms = "channel:add")
    @PostMapping
    public Result<ChannelAccount> create(@RequestBody ChannelAccount account) {
        return Result.ok(channelAccountService.create(account));
    }

    @Operation(summary = "更新账号信息/授权配置",
            description = "更新渠道账号信息或授权配置（整体覆盖提交字段，未提交字段保持原值）。需权限 channel:edit。")
    @ApiResponse(responseCode = "404", description = "渠道账号不存在（业务码 1302）")
    @OperLog(module = "渠道管理", operation = "更新渠道账号")
    @RequirePermission(perms = "channel:edit")
    @PutMapping("/{id}")
    public Result<ChannelAccount> update(@Parameter(description = "渠道账号 ID", required = true) @PathVariable Long id,
                                         @RequestBody ChannelAccount account) {
        account.setId(id);
        return Result.ok(channelAccountService.update(account));
    }

    @Operation(summary = "删除渠道账号",
            description = "删除渠道账号（逻辑删除）。需权限 channel:delete。")
    @ApiResponse(responseCode = "404", description = "渠道账号不存在（业务码 1302）")
    @OperLog(module = "渠道管理", operation = "删除渠道账号")
    @RequirePermission(perms = "channel:delete")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "渠道账号 ID", required = true) @PathVariable Long id) {
        channelAccountService.delete(id);
        return Result.ok();
    }

    @Operation(summary = "刷新授权令牌",
            description = "手动刷新渠道账号的 access_token（token 过期或手动触发场景）。刷新成功返回 true，失败返回 false（如密钥错误/接口异常）。需权限 channel:token。")
    @ApiResponse(responseCode = "404", description = "渠道账号不存在（业务码 1302）")
    @ApiResponse(responseCode = "502", description = "渠道开放平台 API 调用失败（业务码 1304）")
    @OperLog(module = "渠道管理", operation = "刷新渠道令牌")
    @RequirePermission(perms = "channel:token")
    @PostMapping("/{id}/refresh-token")
    public Result<Boolean> refreshToken(@Parameter(description = "渠道账号 ID", required = true) @PathVariable Long id) {
        return Result.ok(channelAccountService.refreshToken(id));
    }

    @Operation(summary = "更新健康状态（状态监控回调）",
            description = "更新渠道账号健康状态/风控等级（供定时巡检或人工标记调用）。需权限 channel:token。")
    @ApiResponse(responseCode = "404", description = "渠道账号不存在（业务码 1302）")
    @RequirePermission(perms = "channel:token")
    @PostMapping("/{id}/health")
    public Result<Void> updateHealth(@Parameter(description = "渠道账号 ID", required = true) @PathVariable Long id,
                                     @Parameter(description = "健康状态：1正常/2受限/3封禁", required = true) @RequestParam Integer healthStatus,
                                     @Parameter(description = "风控等级：0低/1中/2高（可空，传空则不更新）") @RequestParam(required = false) Integer riskLevel) {
        channelAccountService.updateHealthStatus(id, healthStatus, riskLevel);
        return Result.ok();
    }
}
