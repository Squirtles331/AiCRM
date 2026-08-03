package com.aicrm.module.channel.client.douyin.dto;

import lombok.Data;

/**
 * 抖音用户信息
 */
@Data
public class DouyinUserInfo {

    private String openId;

    private String nickname;

    private String avatarUrl;

    /** 性别：0 未知 / 1 男 / 2 女 */
    private Integer gender;

    private String city;
}
