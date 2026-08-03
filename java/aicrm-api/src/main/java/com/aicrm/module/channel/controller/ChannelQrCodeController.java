package com.aicrm.module.channel.controller;

import com.aicrm.common.PageResult;
import com.aicrm.common.Result;
import com.aicrm.common.auth.RequirePermission;
import com.aicrm.module.channel.entity.ChannelQrCode;
import com.aicrm.module.channel.service.ChannelQrCodeService;
import com.aicrm.module.log.annotation.OperLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 渠道活码管理接口：活码生成、扫码统计、来源标记、引流归因
 */
@Tag(name = "渠道活码")
@RestController
@RequestMapping("/api/channel/qr-codes")
@RequiredArgsConstructor
public class ChannelQrCodeController {

    private final ChannelQrCodeService channelQrCodeService;

    @Operation(summary = "活码分页查询",
            description = "渠道活码分页查询，支持按名称关键字/引流渠道账号过滤。需权限 channel:list。")
    @RequirePermission(perms = "channel:list")
    @GetMapping
    public Result<PageResult<ChannelQrCode>> page(
            @Parameter(description = "活码名称关键字（模糊匹配）") @RequestParam(required = false) String keyword,
            @Parameter(description = "引流渠道账号 ID（对应 channel_account.id）") @RequestParam(required = false) Long channelAccountId,
            @Parameter(description = "页码，默认 1") @RequestParam(defaultValue = "1") long page,
            @Parameter(description = "每页条数，默认 20") @RequestParam(defaultValue = "20") long size) {
        return Result.ok(channelQrCodeService.page(keyword, channelAccountId, page, size));
    }

    @Operation(summary = "生成活码",
            description = "生成渠道活码：指定引流渠道账号与落地地址，生成的二维码内容指向 /{id}/scan 扫码入口，用于渠道来源标记与引流归因。需权限 channel:add。\n" +
                    "scene 不填时默认取 渠道ID:账号ID；scanCount/convertedCount 不填默认 0；status 不填默认 1 启用。\n\n" +
                    "请求示例：\n" +
                    "```json\n" +
                    "{\n" +
                    "  \"name\": \"抖音引流活码\",\n" +
                    "  \"channelAccountId\": 1,\n" +
                    "  \"scene\": \"douyin:1\",\n" +
                    "  \"qrUrl\": \"https://example.com/landing?from=qr\",\n" +
                    "  \"status\": 1\n" +
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
                    "    \"name\": \"抖音引流活码\",\n" +
                    "    \"channelAccountId\": 1,\n" +
                    "    \"scene\": \"douyin:1\",\n" +
                    "    \"qrUrl\": \"https://example.com/landing?from=qr\",\n" +
                    "    \"status\": 1,\n" +
                    "    \"scanCount\": 0,\n" +
                    "    \"convertedCount\": 0,\n" +
                    "    \"createdAt\": \"2026-08-03 12:00:00\",\n" +
                    "    \"updatedAt\": \"2026-08-03 12:00:00\"\n" +
                    "  }\n" +
                    "}\n" +
                    "```")
    @ApiResponse(responseCode = "400", description = "活码名称/引流渠道账号不能为空")
    @ApiResponse(responseCode = "404", description = "引流渠道账号不存在（业务码 1302）")
    @OperLog(module = "渠道管理", operation = "生成渠道活码")
    @RequirePermission(perms = "channel:add")
    @PostMapping
    public Result<ChannelQrCode> create(@RequestBody ChannelQrCode qrCode) {
        return Result.ok(channelQrCodeService.create(qrCode));
    }

    @Operation(summary = "更新活码",
            description = "更新渠道活码信息（整体覆盖提交字段，未提交字段保持原值）。需权限 channel:edit。")
    @ApiResponse(responseCode = "404", description = "活码不存在")
    @OperLog(module = "渠道管理", operation = "更新渠道活码")
    @RequirePermission(perms = "channel:edit")
    @PutMapping("/{id}")
    public Result<ChannelQrCode> update(@Parameter(description = "活码 ID", required = true) @PathVariable Long id,
                                        @RequestBody ChannelQrCode qrCode) {
        qrCode.setId(id);
        return Result.ok(channelQrCodeService.update(qrCode));
    }

    @Operation(summary = "删除活码",
            description = "删除渠道活码（逻辑删除），已产生的扫码事件数据保留。需权限 channel:delete。")
    @ApiResponse(responseCode = "404", description = "活码不存在")
    @OperLog(module = "渠道管理", operation = "删除渠道活码")
    @RequirePermission(perms = "channel:delete")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "活码 ID", required = true) @PathVariable Long id) {
        channelQrCodeService.delete(id);
        return Result.ok();
    }

    @Operation(summary = "启停活码",
            description = "启用/停用渠道活码：停用后扫码入口返回 400 业务错误“活码已停用”。需权限 channel:edit。")
    @ApiResponse(responseCode = "404", description = "活码不存在")
    @RequirePermission(perms = "channel:edit")
    @PostMapping("/{id}/status")
    public Result<Void> updateStatus(@Parameter(description = "活码 ID", required = true) @PathVariable Long id,
                                     @Parameter(description = "状态：1启用/0停用", required = true) @RequestParam Integer status) {
        channelQrCodeService.updateStatus(id, status);
        return Result.ok();
    }

    /**
     * 扫码跳转入口：记录扫码 → 302 到落地地址（二维码内容指向此地址）
     */
    @Operation(summary = "扫码跳转入口",
            description = "扫码跳转入口（二维码内容指向此地址）：记录扫码次数并上报扫码事件（event_type=qr，携带 scene 来源标记用于线索归因），随后 302 跳转到落地地址 qrUrl；活码停用时返回 400 业务错误“活码已停用”。公开接口，无登录与权限校验。")
    @ApiResponse(responseCode = "400", description = "活码已停用")
    @ApiResponse(responseCode = "404", description = "活码不存在")
    @GetMapping("/{id}/scan")
    public void scan(@Parameter(description = "活码 ID", required = true) @PathVariable Long id,
                     @Parameter(description = "渠道来源标记（扫码场景），不传则取活码自带 scene") @RequestParam(required = false) String scene,
                     HttpServletResponse response) throws IOException {
        String target = channelQrCodeService.recordScan(id, scene);
        if (StringUtils.hasText(target)) {
            response.sendRedirect(target);
        } else {
            response.setStatus(200);
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write("ok");
        }
    }
}
