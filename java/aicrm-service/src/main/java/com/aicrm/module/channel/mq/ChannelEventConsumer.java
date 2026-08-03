package com.aicrm.module.channel.mq;

import com.aicrm.common.constant.MqConstants;
import com.aicrm.common.context.TenantContext;
import com.aicrm.module.channel.entity.ChannelEvent;
import com.aicrm.module.channel.service.ChannelEventMappingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 渠道事件消费者：事件 → 线索映射
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChannelEventConsumer {

    private final ChannelEventMappingService mappingService;

    @RabbitListener(queues = MqConstants.QUEUE_CHANNEL_EVENT)
    public void onChannelEvent(ChannelEvent event) {
        // 异步线程无 Web 上下文：显式设置租户，保证 SQL 自动注入 tenant_id
        TenantContext.setTenantId(event == null ? null : event.getTenantId());
        try {
            mappingService.mapToLead(event);
        } catch (Exception e) {
            // 重试由 MQ 投递机制处理；此处记录，避免消息丢失静默
            log.error("渠道事件映射失败 eventId={}, err={}",
                    event == null ? null : event.getExternalEventId(), e.getMessage(), e);
            throw e;
        } finally {
            TenantContext.clear();
        }
    }
}
