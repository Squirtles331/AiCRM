package com.aicrm.module.channel.gateway;

/**
 * 统一消息网关：全渠道消息统一发送入口，内置 敏感词拦截 + 频率限流 + 平台风控规则适配
 */
public interface MessageGatewayService {

    /**
     * 发送文本消息（敏感词校验 → 风控检查 → 限流 → 按渠道分发 → 记录发送事件）
     *
     * @param channelAccountId 渠道账号 ID
     * @param toExternalUserId 目标用户（渠道侧 open_id / external_userid）
     * @param content          文本内容
     * @throws com.aicrm.common.exception.BusinessException 触发风控/敏感词/限流时抛出
     */
    void sendText(Long channelAccountId, String toExternalUserId, String content);
}
