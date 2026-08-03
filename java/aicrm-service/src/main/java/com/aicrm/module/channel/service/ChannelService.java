package com.aicrm.module.channel.service;

import com.aicrm.module.channel.entity.Channel;

import java.util.List;

/**
 * 渠道定义服务（平台级）
 */
public interface ChannelService {

    /**
     * 启用渠道列表（前端下拉）
     */
    List<Channel> listEnabled();
}
