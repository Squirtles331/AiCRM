package com.aicrm.module.channel.entity;

import com.aicrm.common.base.BaseEntity;
import com.aicrm.common.handler.JsonbTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 渠道原始事件（三渠道统一入口，幂等去重）
 * <p>唯一约束 (tenant_id, channel_account_id, external_event_id) 见 db/init.sql
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "channel_event", autoResultMap = true)
@Schema(description = "渠道原始事件（三渠道统一入口，幂等去重）")
public class ChannelEvent extends BaseEntity {

    /** 租户 ID */
    @Schema(description = "租户 ID", example = "1")
    private Long tenantId;

    /** 渠道账号 ID */
    @Schema(description = "渠道账号 ID（对应 channel_account.id）", example = "1")
    private Long channelAccountId;

    /** 事件类型：comment/dm/form/click/lead */
    @Schema(description = "事件类型：comment评论/dm私信/form表单/click点击/lead线索（活码扫码内部事件为 qr，不入此接口）", example = "dm")
    private String eventType;

    /** 渠道事件唯一 ID（去重键） */
    @Schema(description = "渠道事件唯一 ID（去重键，与 tenantId+channelAccountId 组合幂等）", example = "evt_20260803_001")
    private String externalEventId;

    /** 渠道侧用户 ID */
    @Schema(description = "渠道侧用户 ID", example = "openid_xxx")
    private String externalUserId;

    /** 原始事件数据（JSONB） */
    @TableField(typeHandler = JsonbTypeHandler.class)
    @Schema(description = "原始事件数据（JSONB），存储渠道回调的完整原始 JSON，字段随渠道与事件类型不同")
    private String rawPayload;

    /** 是否已映射落线索：0 未处理 / 1 已映射 */
    @Schema(description = "是否已映射落线索：0未处理/1已映射", example = "0")
    private Integer mapped;
}
