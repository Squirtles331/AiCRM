package com.aicrm.module.channel.client.wecom.dto;

import lombok.Data;

import java.util.List;

/**
 * 企微外部联系人（客户）信息
 */
@Data
public class WecomExternalContact {

    private String externalUserId;

    private String name;

    private String avatarUrl;

    /** 企业名称 */
    private String corpName;

    /** 客户标签（企业标签 + 个人标签） */
    private List<String> tags;
}
