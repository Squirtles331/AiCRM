package com.aicrm.module.conversation.entity;

import com.aicrm.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 会话（承接阵地：企微/WhatsApp/私信）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("conversation")
@Schema(description = "会话（承接阵地：企微/WhatsApp/私信）")
public class Conversation extends BaseEntity {

    /** 租户 ID */
    @Schema(description = "租户 ID", example = "1")
    private Long tenantId;

    /** 承接渠道账号 ID */
    @Schema(description = "承接渠道账号 ID", example = "1")
    private Long channelAccountId;

    /** 联系人 ID */
    @Schema(description = "联系人 ID", example = "1")
    private Long contactId;

    /** 线索 ID */
    @Schema(description = "线索 ID", example = "1")
    private Long leadId;

    /** 会话类型：dm/wecom_chat/whatsapp */
    @Schema(description = "会话类型：dm直接私信/wecom_chat企微聊天/whatsapp", example = "wecom_chat")
    private String conversationType;

    /** 状态：active/transferred/closed/archived */
    @Schema(description = "状态：active进行中/transferred已转人工/closed已关闭/archived已归档", example = "active")
    private String status;

    /** 当前人工处理人 ID */
    @Schema(description = "当前人工处理人 ID", example = "8")
    private Long assignedTo;

    /** 最后消息时间 */
    @Schema(description = "最后消息时间")
    private LocalDateTime lastMessageAt;
}
