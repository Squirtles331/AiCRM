package com.aicrm.module.channel.entity;

import com.aicrm.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 渠道活码：扫码引流 + 渠道来源标记 + 引流归因
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("channel_qr_code")
@Schema(description = "渠道活码（扫码引流/渠道来源标记/引流归因）")
public class ChannelQrCode extends BaseEntity {

    /** 租户 ID */
    @Schema(description = "租户 ID", example = "1")
    private Long tenantId;

    /** 活码名称 */
    @Schema(description = "活码名称", example = "抖音引流活码")
    private String name;

    /** 引流目标渠道账号 */
    @Schema(description = "引流目标渠道账号 ID", example = "1")
    private Long channelAccountId;

    /** 渠道来源标记（扫码事件携带，用于线索归因） */
    @Schema(description = "渠道来源标记（扫码事件携带，用于线索归因），不填默认取 渠道ID:账号ID", example = "1:1")
    private String scene;

    /** 扫码跳转落地地址 */
    @Schema(description = "扫码跳转落地地址", example = "https://example.com/landing?from=qr")
    private String qrUrl;

    /** 状态：1 启用 / 0 停用 */
    @Schema(description = "状态：1启用/0停用", example = "1")
    private Integer status;

    /** 扫码次数 */
    @Schema(description = "扫码次数", example = "0")
    private Integer scanCount;

    /** 转化数（扫码后转化为线索数，3.2 客户身份归一联动） */
    @Schema(description = "转化数（扫码后转化为线索数，与客户身份归一联动）", example = "0")
    private Integer convertedCount;
}
