package com.aicrm.module.wecom.controller;

import com.aicrm.common.PageResult;
import com.aicrm.common.Result;
import com.aicrm.common.auth.RequirePermission;
import com.aicrm.module.product.entity.Product;
import com.aicrm.module.wecom.service.WecomSidebarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 企微侧边栏业务接口（3.3.5）：客户画像 / 历史会话 / 推荐话术 / 产品资料快捷发送
 */
@Tag(name = "企微侧边栏")
@RestController
@RequestMapping("/api/wecom/sidebar")
@RequiredArgsConstructor
public class WecomSidebarController {

    private final WecomSidebarService wecomSidebarService;

    @Operation(summary = "客户画像",
            description = "企微侧边栏 - 客户画像（权限 wecom:sidebar）：按企微外部用户 ID 聚合身份→线索→客户信息、标签、最近意向。")
    @RequirePermission(perms = "wecom:sidebar")
    @GetMapping("/profile")
    public Result<WecomSidebarService.CustomerProfile> profile(
            @Parameter(description = "企微外部用户 ID", required = true) @RequestParam String externalUserId) {
        return Result.ok(wecomSidebarService.profile(externalUserId));
    }

    @Operation(summary = "历史会话",
            description = "企微侧边栏 - 历史会话（权限 wecom:sidebar）：返回该企微客户关联的会话列表（含最近一条消息）。")
    @RequirePermission(perms = "wecom:sidebar")
    @GetMapping("/history")
    public Result<List<WecomSidebarService.ConversationBrief>> history(
            @Parameter(description = "企微外部用户 ID", required = true) @RequestParam String externalUserId) {
        return Result.ok(wecomSidebarService.history(externalUserId));
    }

    @Operation(summary = "推荐话术",
            description = "企微侧边栏 - 推荐话术（权限 wecom:sidebar）：按意向返回建议话术与缺失字段；intent 不传时取线索最近意向。\n\n"
                    + "intent 枚举：quote报价/sample样品/selection选型/other其他。")
    @RequirePermission(perms = "wecom:sidebar")
    @GetMapping("/reply-suggestions")
    public Result<WecomSidebarService.ReplySuggestion> replySuggestions(
            @Parameter(description = "企微外部用户 ID", required = true) @RequestParam String externalUserId,
            @Parameter(description = "意向：quote报价/sample样品/selection选型/other其他（可空，默认取线索最近意向）") @RequestParam(required = false) String intent) {
        return Result.ok(wecomSidebarService.replySuggestions(externalUserId, intent));
    }

    @Operation(summary = "产品资料（快捷发送）",
            description = "企微侧边栏 - 产品资料分页（权限 wecom:sidebar）：仅展示上架产品，供坐席快捷发送。")
    @RequirePermission(perms = "wecom:sidebar")
    @GetMapping("/products")
    public Result<PageResult<Product>> products(
            @Parameter(description = "产品名称关键字（可空）") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码，默认 1") @RequestParam(defaultValue = "1") long page,
            @Parameter(description = "每页条数，默认 20") @RequestParam(defaultValue = "20") long size) {
        return Result.ok(wecomSidebarService.products(keyword, page, size));
    }
}
