package com.aicrm.module.system.entity;

import com.aicrm.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户-角色关联
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user_role")
public class UserRole extends BaseEntity {

    /** 租户 ID */
    private Long tenantId;

    /** 用户 ID */
    private Long userId;

    /** 角色 ID */
    private Long roleId;
}
