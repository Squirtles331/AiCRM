package com.aicrm.module.tag.entity;

import com.aicrm.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户标签定义（3.2.3）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("customer_tag")
@Schema(description = "客户标签定义（3.2.3）")
public class CustomerTag extends BaseEntity {

    /** 租户 ID */
    @Schema(description = "租户 ID")
    private Long tenantId;

    /** 标签名称 */
    @Schema(description = "标签名称", example = "高意向客户")
    private String name;

    /** 标签颜色（前端展示） */
    @Schema(description = "标签颜色（前端展示，如 #FF5733）", example = "#FF5733")
    private String color;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 状态：1 启用 / 0 停用 */
    @Schema(description = "状态：1 启用 / 0 停用", example = "1")
    private Integer status;
}
