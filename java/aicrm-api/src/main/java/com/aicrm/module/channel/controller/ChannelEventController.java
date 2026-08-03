package com.aicrm.module.channel.controller;

import com.aicrm.common.Result;
import com.aicrm.module.channel.entity.ChannelEvent;
import com.aicrm.module.channel.service.ChannelEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 渠道事件接入接口（三渠道 Webhook 统一入口）
 */
@Tag(name = "渠道事件")
@RestController
@RequestMapping("/api/channel/events")
@RequiredArgsConstructor
public class ChannelEventController {

    private final ChannelEventService channelEventService;

    @Operation(summary = "接收渠道事件（Webhook）",
            description = "三渠道 Webhook 统一入口：接收抖音/视频号/企业微信等渠道推送的事件，按 (tenantId+channelAccountId+externalEventId) 幂等去重后入库并发布异步事件。\n" +
                    "返回 true 表示首次接收并已入库；返回 false 表示重复事件被忽略（不重复落库）。\n" +
                    "eventType 取值：comment评论/dm私信/form表单/click点击/lead线索。\n" +
                    "rawPayload（JSONB）存储渠道回调的完整原始 JSON，字段随渠道与事件类型不同。\n" +
                    "该接口为渠道平台回调入口，无登录与权限校验（本版本为模拟实现，未做渠道签名校验）。")
    @PostMapping
    public Result<Boolean> receive(@RequestBody ChannelEvent event) {
        return Result.ok(channelEventService.receiveEvent(event));
    }
}
