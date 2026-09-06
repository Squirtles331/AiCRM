package com.aicrm.module.conversation.dto;

import com.aicrm.module.conversation.entity.Message;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 转人工交接包：坐席接手时查看的基础会话上下文。
 */
@Data
@Schema(description = "转人工交接包：基础会话信息、人工维护的线索意向和最近消息")
public class TransferPackage {

    @Schema(description = "会话 ID", example = "100")
    private Long conversationId;

    @Schema(description = "租户 ID", example = "1")
    private Long tenantId;

    @Schema(description = "线索 ID", example = "50")
    private Long leadId;

    @Schema(description = "会话类型：dm直接私信/wecom_chat企微聊天/whatsapp", example = "wecom_chat")
    private String conversationType;

    @Schema(description = "会话状态：active进行中/transferred已转人工/closed已关闭/archived已归档", example = "transferred")
    private String status;

    @Schema(description = "接手坐席 ID", example = "8")
    private Long operatorId;

    @Schema(description = "人工维护的线索意向：quote报价/sample样品/selection选型/other其他", example = "quote")
    private String intent;

    @Schema(description = "最近 20 条消息，按时间正序排列")
    private List<Message> recentMessages;
}
