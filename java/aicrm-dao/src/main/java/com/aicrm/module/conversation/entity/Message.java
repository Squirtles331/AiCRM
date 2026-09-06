package com.aicrm.module.conversation.entity;

import com.aicrm.common.base.BaseEntity;
import com.aicrm.common.handler.JsonbTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 消息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "message", autoResultMap = true)
@Schema(description = "消息")
public class Message extends BaseEntity {

    /** 租户 ID */
    @Schema(description = "租户 ID", example = "1")
    private Long tenantId;

    /** 会话 ID */
    @Schema(description = "会话 ID", example = "100")
    private Long conversationId;

    /** 发送方：customer/human/system */
    @Schema(description = "发送方：customer客户/human人工/system系统", example = "customer")
    private String senderType;

    /** 消息内容 */
    @Schema(description = "消息内容")
    private String content;

    /** 消息类型：text/image/file/card */
    @Schema(description = "消息类型：text文本/image图片/file文件/card卡片", example = "text")
    private String msgType;

    /** 附件 ID 列表（JSONB） */
    @Schema(description = "附件 ID 列表（JSONB 字符串）", example = "[]")
    @TableField(typeHandler = JsonbTypeHandler.class)
    private String attachments;

    /** 原始消息（JSONB） */
    @Schema(description = "原始消息（JSONB 字符串）", example = "{}")
    @TableField(typeHandler = JsonbTypeHandler.class)
    private String raw;
}
