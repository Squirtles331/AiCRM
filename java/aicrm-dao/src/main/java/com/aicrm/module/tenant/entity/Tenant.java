package com.aicrm.module.tenant.entity;

import com.aicrm.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 租户（企业）—— 订阅模式根实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tenant")
@Schema(description = "租户（企业）—— 订阅模式根实体")
public class Tenant extends BaseEntity {

    /** 企业名称 */
    @Schema(description = "企业名称")
    private String name;

    /** 套餐编码：starter/pro/enterprise */
    @Schema(description = "套餐编码：starter/pro/enterprise", example = "starter")
    private String planCode;

    /** 坐席数上限 */
    @Schema(description = "坐席数上限")
    private Integer seatCount;

    /** 到期时间，为空表示长期有效 */
    @Schema(description = "到期时间，为空表示长期有效")
    private LocalDateTime expireAt;

    /** 平台对接联系人 */
    @Schema(description = "平台对接联系人")
    private String contactName;

    /** 平台对接联系电话 */
    @Schema(description = "平台对接联系电话")
    private String contactMobile;

    /** 状态：1 启用 / 0 停用 */
    @Schema(description = "状态：1启用/0停用", example = "1")
    private Integer status;
}
