package com.aicrm.module.channel.mq;

import com.aicrm.common.constant.MqConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * 死信队列监听器：业务队列重试耗尽（默认 3 次）后的兜底处理
 * <p>
 * 死信消息不做自动重投，记录告警日志供人工介入（后续可扩展：告警通知 / 手动重投 API）。
 * 消息头 x-death 携带原始队列、被拒次数与原因，便于定位问题根因。
 */
@Slf4j
@Component
public class DlqConsumer {

    @RabbitListener(queues = MqConstants.DLQ)
    public void onDeadLetter(Message message) {
        MessageProperties props = message.getMessageProperties();
        Object xDeath = props.getHeader("x-death");
        Object originalQueue = props.getHeader("x-original-queue");
        String body = message.getBody() == null ? "" : new String(message.getBody(), StandardCharsets.UTF_8);
        log.error("[死信队列] 消息重试耗尽，需人工介入。originalQueue={}, routingKey={}, x-death={}, body={}",
                originalQueue, props.getReceivedRoutingKey(), xDeath, truncate(body));
    }

    private String truncate(String body) {
        return body.length() <= 2000 ? body : body.substring(0, 2000) + "...(truncated)";
    }
}
