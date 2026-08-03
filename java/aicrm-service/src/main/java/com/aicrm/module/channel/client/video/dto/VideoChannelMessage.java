package com.aicrm.module.channel.client.video.dto;

import lombok.Data;

/**
 * 视频号私信消息
 */
@Data
public class VideoChannelMessage {

    private String messageId;

    /** 发送方 open_id */
    private String fromOpenId;

    private String content;

    /** 消息时间（Unix 秒） */
    private Long createTime;
}
