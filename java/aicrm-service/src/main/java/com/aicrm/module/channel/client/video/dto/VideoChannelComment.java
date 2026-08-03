package com.aicrm.module.channel.client.video.dto;

import lombok.Data;

/**
 * 视频号评论
 */
@Data
public class VideoChannelComment {

    private String commentId;

    /** 评论用户 open_id */
    private String openId;

    private String content;

    /** 评论时间（Unix 秒） */
    private Long createTime;
}
