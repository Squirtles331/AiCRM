package com.aicrm.module.channel.client.wecom.dto;

import lombok.Data;

/**
 * 企微企业客户标签
 */
@Data
public class WecomCorpTag {

    private String tagId;

    private String tagName;

    /** 所属标签组 ID */
    private String groupId;

    private String groupName;
}
