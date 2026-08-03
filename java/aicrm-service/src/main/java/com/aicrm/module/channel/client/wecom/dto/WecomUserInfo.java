package com.aicrm.module.channel.client.wecom.dto;

import lombok.Data;

/**
 * 企微侧边栏 OAuth 鉴权后获取的成员身份
 */
@Data
public class WecomUserInfo {

    private String userId;

    private String name;

    private String avatarUrl;

    private String mobile;
}
