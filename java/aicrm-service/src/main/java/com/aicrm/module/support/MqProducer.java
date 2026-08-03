package com.aicrm.module.support;

import com.aicrm.common.constant.MqConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * 消息发送模板：统一 RabbitMQ 事件发布入口
 * <p>
 * 事件约定：
 * - 交换器：aicrm.events（topic）
 * - 路由键：channel.event.* / ai.analysis.* / workflow.*
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MqProducer {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 发送事件到事件总线
     *
     * @param routingKey 路由键（如 channel.event.new）
     * @param payload    事件体（自动 JSON 序列化）
     */
    public void send(String routingKey, Object payload) {
        rabbitTemplate.convertAndSend(MqConstants.EVENT_EXCHANGE, routingKey, payload);
        log.debug("事件已发布 routingKey={}, payloadClass={}", routingKey, payload == null ? null : payload.getClass().getSimpleName());
    }
}
