package com.aicrm.module.tenant.entity;

import com.aicrm.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 套餐定义（平台级，租户订阅的版本配置）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_plan")
@Schema(description = "套餐定义（平台级，租户订阅的版本配置）")
public class Plan extends BaseEntity {

    /** 套餐编码：starter/pro/enterprise */
    @Schema(description = "套餐编码：starter/pro/enterprise", example = "starter")
    private String code;

    /** 套餐名称 */
    @Schema(description = "套餐名称")
    private String name;

    /** 坐席数上限 */
    @Schema(description = "坐席数上限")
    private Integer seatCount;

    /** 月度 AI 调用额度 */
    @Schema(description = "月度 AI 调用额度")
    private Long aiQuotaMonth;

    /** 月单价（元） */
    @Schema(description = "月单价（元）")
    private BigDecimal monthlyPrice;

    /** 套餐描述 */
    @Schema(description = "套餐描述")
    private String description;

    /** 状态：1 上架 / 0 下架 */
    @Schema(description = "状态：1上架/0下架", example = "1")
    private Integer status;
}
