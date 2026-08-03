package com.aicrm.module.channel.client.douyin.dto;

import lombok.Data;

/**
 * 抖音作品
 */
@Data
public class DouyinVideo {

    private String videoId;

    private String title;

    /** 封面图 URL */
    private String coverUrl;

    /** 发布时间（Unix 秒） */
    private Long createTime;

    private Long likeCount;

    private Long commentCount;

    private Long shareCount;
}
