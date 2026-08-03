package com.aicrm.module.channel.service;

import com.aicrm.module.channel.entity.ChannelEvent;

/**
 * 渠道事件 → 线索 映射服务
 */
public interface ChannelEventMappingService {

    /**
     * 将渠道事件映射为线索（M1：创建线索并关联来源；后续叠加身份合并/评分）
     *
     * @param event 渠道事件
     * @return 是否成功映射
     */
    boolean mapToLead(ChannelEvent event);
}
