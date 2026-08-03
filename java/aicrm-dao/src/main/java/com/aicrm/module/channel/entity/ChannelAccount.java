package com.aicrm.module.channel.entity;

import com.aicrm.common.base.BaseEntity;
import com.aicrm.common.handler.JsonbTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 渠道账号（企业号/矩阵号）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "channel_account", autoResultMap = true)
@Schema(description = "渠道账号（企业号/矩阵号）")
public class ChannelAccount extends BaseEntity {

    /** 租户 ID */
    @Schema(description = "租户 ID", example = "1")
    private Long tenantId;

    /** 渠道 ID */
    @Schema(description = "渠道 ID（对应 channel.id）", example = "1")
    private Long channelId;

    /** 账号名称 */
    @Schema(description = "账号名称", example = "品牌官方号")
    private String accountName;

    /** 渠道侧账号 ID */
    @Schema(description = "渠道侧账号 ID", example = "douyin_open_id_xxx")
    private String externalId;

    /** 授权凭证（加密存储，JSONB） */
    @TableField(typeHandler = JsonbTypeHandler.class)
    @Schema(description = "授权凭证（加密存储，JSONB），结构：{\"accessToken\":\"访问令牌\",\"refreshToken\":\"刷新令牌\",\"expireAt\":\"过期时间epoch毫秒\"}，抖音另含 appId/secret、企业微信另含 corpId/corpSecret，按渠道可扩展")
    private String authConfig;

    /** 账号健康状态：1 正常 / 2 受限 / 3 封禁 */
    @Schema(description = "账号健康状态：1正常/2受限/3封禁", example = "1")
    private Integer healthStatus;

    /** 风控等级：0 低 / 1 中 / 2 高 */
    @Schema(description = "风控等级：0低/1中/2高", example = "0")
    private Integer riskLevel;
}
