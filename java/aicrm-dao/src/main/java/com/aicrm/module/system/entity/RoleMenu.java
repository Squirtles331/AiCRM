package com.aicrm.module.system.entity;

import com.aicrm.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色-菜单关联
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role_menu")
public class RoleMenu extends BaseEntity {

    /** 租户 ID */
    private Long tenantId;

    /** 角色 ID */
    private Long roleId;

    /** 菜单 ID */
    private Long menuId;
}
