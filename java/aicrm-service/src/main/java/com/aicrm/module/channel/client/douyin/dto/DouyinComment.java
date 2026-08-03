package com.aicrm.module.channel.client.douyin.dto;

import lombok.Data;

/**
 * 抖音评论
 */
@Data
public class DouyinComment {

    private String commentId;

    /** 评论用户 open_id */
    private String openId;

    private String content;

    /** 评论时间（Unix 秒） */
    private Long createTime;

    /** 该评论被回复数量 */
    private Long replyCommentTotal;
}
