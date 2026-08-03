package com.aicrm.module.competitor.entity;

import com.aicrm.common.base.BaseEntity;
import com.aicrm.common.handler.JsonbTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 竞品产品参数（3.4.2）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "competitor_product", autoResultMap = true)
@Schema(description = "竞品产品参数")
public class CompetitorProduct extends BaseEntity {

    /** 租户 ID */
    @Schema(description = "租户 ID")
    private Long tenantId;

    /** 竞品 ID */
    @Schema(description = "竞品 ID")
    private Long competitorId;

    /** 竞品产品名称 */
    @Schema(description = "竞品产品名称", example = "竞品云版")
    private String productName;

    /** 规格型号 */
    @Schema(description = "规格型号", example = "旗舰版")
    private String spec;

    /** 参考价格 */
    @Schema(description = "参考价格（元）", example = "2999")
    private BigDecimal price;

    /** 竞品产品参数（JSONB） */
    @Schema(description = "竞品产品参数（JSONB 对象，结构化键值对，如 {\"并发数\":\"300\",\"存储\":\"50GB\"}）", example = "{\"并发数\":\"300\",\"存储\":\"50GB\"}")
    @TableField(typeHandler = JsonbTypeHandler.class)
    private String params;
}
