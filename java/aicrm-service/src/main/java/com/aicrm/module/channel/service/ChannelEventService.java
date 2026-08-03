package com.aicrm.module.channel.service;

import com.aicrm.module.channel.entity.ChannelEvent;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 渠道事件服务
 */
public interface ChannelEventService extends IService<ChannelEvent> {

    /**
     * 接收渠道事件：幂等去重落库，并发布 MQ 事件 {@code channel.event.new}
     *
     * @param event 渠道事件
     * @return true=首次入库（已发布事件）；false=重复事件（忽略）
     */
    boolean receiveEvent(ChannelEvent event);
}
