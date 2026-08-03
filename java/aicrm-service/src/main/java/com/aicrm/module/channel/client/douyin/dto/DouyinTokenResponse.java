package com.aicrm.module.channel.client.douyin.dto;

import lombok.Data;

/**
 * 抖音 access_token 刷新结果
 */
@Data
public class DouyinTokenResponse {

    private String accessToken;

    private String refreshToken;

    /** 有效期（秒） */
    private Long expiresIn;

    private String openId;
}
