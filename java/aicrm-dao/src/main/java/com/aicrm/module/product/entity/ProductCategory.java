package com.aicrm.module.product.entity;

import com.aicrm.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 产品分类（3.4.1）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product_category")
@Schema(description = "产品分类")
public class ProductCategory extends BaseEntity {

    /** 租户 ID */
    @Schema(description = "租户 ID")
    private Long tenantId;

    /** 分类名称 */
    @Schema(description = "分类名称", example = "CRM 产品线")
    private String name;

    /** 父分类 ID（0 为顶级） */
    @Schema(description = "父分类 ID（0 为顶级）", example = "0")
    private Long parentId;

    /** 排序（小在前） */
    @Schema(description = "排序（小在前）", example = "1")
    private Integer sort;

    /** 状态：1 启用 / 0 停用 */
    @Schema(description = "状态：1 启用 / 0 停用", example = "1")
    private Integer status;
}
