package com.aicrm.module.document.entity;

import com.aicrm.common.base.BaseEntity;
import com.aicrm.common.handler.JsonbTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 资料/文档（产品手册/案例/白皮书/选型表）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "document", autoResultMap = true)
@Schema(description = "资料文档")
public class Document extends BaseEntity {

    /** 租户 ID */
    @Schema(description = "租户 ID")
    private Long tenantId;

    /** 标题 */
    @Schema(description = "标题", example = "AI 外呼系统产品手册")
    private String title;

    /** 类型：product_brochure/case/whitepaper/selection_table */
    @Schema(description = "类型：product_brochure产品手册/case案例/whitepaper白皮书/selection_table选型表", example = "product_brochure")
    private String docType;

    /** 文件地址（对象存储/本地） */
    @Schema(description = "文件地址（对象存储/本地）", example = "/uploads/xxx.pdf")
    private String fileUrl;

    /** 版本号 */
    @Schema(description = "版本号", example = "1")
    private Integer version;

    /** 状态：0 草稿 / 1 已发布 */
    @Schema(description = "状态：0 草稿 / 1 已发布", example = "0")
    private Integer status;

    /** 标签（JSONB） */
    @TableField(typeHandler = JsonbTypeHandler.class)
    @Schema(description = "标签（JSON 数组字符串）", example = "[\"AI\",\"外呼\"]")
    private String tags;
}
