package com.aicrm.module.product.entity;

import com.aicrm.common.base.BaseEntity;
import com.aicrm.common.handler.JsonbTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 产品资料（3.3.5 侧边栏快捷发送 / 3.4.1 产品库管理）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "product", autoResultMap = true)
@Schema(description = "产品资料")
public class Product extends BaseEntity {

    /** 租户 ID */
    @Schema(description = "租户 ID")
    private Long tenantId;

    /** 分类 ID */
    @Schema(description = "分类 ID")
    private Long categoryId;

    /** 产品名称 */
    @Schema(description = "产品名称", example = "企业版 CRM")
    private String name;

    /** 产品编码 */
    @Schema(description = "产品编码（SKU）", example = "CRM-ENT-001")
    private String sku;

    /** 规格型号 */
    @Schema(description = "规格型号", example = "标准版")
    private String spec;

    /** 参考价格 */
    @Schema(description = "参考价格（元）", example = "1999")
    private BigDecimal price;

    /** 产品参数（结构化键值，JSONB） */
    @Schema(description = "产品参数（JSONB 对象，结构化键值对，如 {\"容量\":\"100GB\",\"并发数\":\"500\"}）", example = "{\"容量\":\"100GB\",\"并发数\":\"500\"}")
    @TableField(typeHandler = JsonbTypeHandler.class)
    private String params;

    /** 附件列表（URL/名称数组，JSONB） */
    @Schema(description = "附件列表（JSONB 数组，元素含 name 名称/url 地址，如 [{\"name\":\"选型表.xlsx\",\"url\":\"/uploads/xxx.xlsx\"}]）", example = "[{\"name\":\"选型表.xlsx\",\"url\":\"/uploads/xxx.xlsx\"}]")
    @TableField(typeHandler = JsonbTypeHandler.class)
    private String attachments;

    /** 产品描述 */
    @Schema(description = "产品描述")
    private String description;

    /** 状态：1 上架 / 0 下架 */
    @Schema(description = "状态：1 上架 / 0 下架", example = "1")
    private Integer status;
}
