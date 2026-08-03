package com.aicrm.module.channel.controller;

import com.aicrm.common.Result;
import com.aicrm.common.auth.RequirePermission;
import com.aicrm.module.channel.entity.Channel;
import com.aicrm.module.channel.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 渠道定义接口（平台级渠道列表）
 */
@Tag(name = "渠道管理")
@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    @Operation(summary = "启用渠道列表（前端下拉）",
            description = "获取全部启用状态的渠道定义列表，供前端下拉/筛选使用。需权限 channel:list。\n" +
                    "渠道编码 code 取值：douyin抖音 / video_channel视频号 / tiktok TikTok / wecom企业微信 / whatsapp WhatsApp。")
    @RequirePermission(perms = "channel:list")
    @GetMapping
    public Result<List<Channel>> listEnabled() {
        return Result.ok(channelService.listEnabled());
    }
}
