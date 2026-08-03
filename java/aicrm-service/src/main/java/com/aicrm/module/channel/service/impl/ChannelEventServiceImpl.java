package com.aicrm.module.channel.service.impl;

import com.aicrm.common.constant.MqConstants;
import com.aicrm.module.channel.entity.ChannelEvent;
import com.aicrm.module.channel.mapper.ChannelEventMapper;
import com.aicrm.module.channel.service.ChannelEventService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * 渠道事件服务实现：幂等入库 + 事件发布
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChannelEventServiceImpl extends ServiceImpl<ChannelEventMapper, ChannelEvent>
        implements ChannelEventService {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public boolean receiveEvent(ChannelEvent event) {
        // 幂等：同一 (tenant, channelAccount, externalEventId) 只处理一次
        Long exists = this.lambdaQuery()
                .eq(ChannelEvent::getTenantId, event.getTenantId())
                .eq(ChannelEvent::getChannelAccountId, event.getChannelAccountId())
                .eq(ChannelEvent::getExternalEventId, event.getExternalEventId())
                .select(ChannelEvent::getId)
                .last("LIMIT 1")
                .list()
                .stream().findFirst().map(ChannelEvent::getId).orElse(null);
        if (exists != null) {
            log.info("渠道事件重复，忽略 tenantId={}, accountId={}, eventId={}",
                    event.getTenantId(), event.getChannelAccountId(), event.getExternalEventId());
            return false;
        }
        if (event.getMapped() == null) {
            event.setMapped(0);
        }
        this.save(event);

        // 发布事件，触发映射与后续工作流（异步消费）
        rabbitTemplate.convertAndSend(MqConstants.EVENT_EXCHANGE, "channel.event.new", event);
        log.info("渠道事件已入库并发布 tenantId={}, eventId={}, type={}",
                event.getTenantId(), event.getExternalEventId(), event.getEventType());
        return true;
    }
}
