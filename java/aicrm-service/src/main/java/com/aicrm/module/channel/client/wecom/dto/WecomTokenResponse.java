package com.aicrm.module.channel.client.wecom.dto;

import lombok.Data;

/**
 * 企业微信 access_token 响应
 */
@Data
public class WecomTokenResponse {

    private String accessToken;

    /** 有效期（秒），默认 7200 */
    private Long expiresIn;
}
