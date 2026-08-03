package com.aicrm.module.system.entity;

import com.aicrm.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色（租户内）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
@Schema(description = "角色（租户内）")
public class Role extends BaseEntity {

    /** 租户 ID */
    @Schema(description = "租户 ID")
    private Long tenantId;

    /** 角色编码：admin/sales/supervisor 或自定义 */
    @Schema(description = "角色编码：admin/sales/supervisor 或自定义", example = "sales")
    private String code;

    /** 角色名称 */
    @Schema(description = "角色名称")
    private String name;

    /** 角色描述 */
    @Schema(description = "角色描述")
    private String description;

    /** 状态：1 启用 / 0 停用 */
    @Schema(description = "状态：1启用/0停用", example = "1")
    private Integer status;
}
