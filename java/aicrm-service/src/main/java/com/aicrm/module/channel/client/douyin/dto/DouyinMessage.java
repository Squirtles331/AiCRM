package com.aicrm.module.channel.client.douyin.dto;

import lombok.Data;

/**
 * 抖音私信消息
 */
@Data
public class DouyinMessage {

    private String messageId;

    /** 发送方 open_id */
    private String fromOpenId;

    private String content;

    /** 消息时间（Unix 秒） */
    private Long createTime;
}
