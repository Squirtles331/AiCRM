package com.aicrm.module.channel.entity;

import com.aicrm.common.base.BaseEntity;
import com.aicrm.common.handler.JsonbTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 渠道定义（抖音/视频号/TikTok/企微/WhatsApp）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "channel", autoResultMap = true)
@Schema(description = "渠道定义（平台级，抖音/视频号/TikTok/企业微信/WhatsApp）")
public class Channel extends BaseEntity {

    /** 渠道编码：douyin/video_channel/tiktok/wecom/whatsapp */
    @Schema(description = "渠道编码：douyin抖音/video_channel视频号/tiktok TikTok/wecom企业微信/whatsapp WhatsApp", example = "douyin")
    private String code;

    /** 渠道名称 */
    @Schema(description = "渠道名称", example = "抖音")
    private String name;

    /** 渠道类型：short_video/social/im */
    @Schema(description = "渠道类型：short_video短视频/social社交媒体/im即时通讯", example = "short_video")
    private String type;

    /** 配置项 schema（JSONB） */
    @TableField(typeHandler = JsonbTypeHandler.class)
    @Schema(description = "配置项 schema（JSONB），定义该渠道接入所需配置项（如 appId/secret 等键）的 JSON 结构")
    private String configSchema;

    /** 状态：1 启用 / 0 停用 */
    @Schema(description = "状态：1启用/0停用", example = "1")
    private Integer status;
}
