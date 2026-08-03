package com.aicrm.module.system.mapper;

import com.aicrm.module.system.entity.Role;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色 Mapper
 */
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 查询用户拥有的角色（用户-角色关联，仅启用角色）
     */
    @InterceptorIgnore(tenantLine = "true")
    @Select("""
            SELECT r.* FROM sys_role r
            JOIN sys_user_role ur ON ur.role_id = r.id AND ur.deleted = 0
            WHERE ur.user_id = #{userId}
              AND ur.tenant_id = #{tenantId}
              AND r.deleted = 0 AND r.status = 1
            """)
    List<Role> selectRolesByUserId(@Param("userId") Long userId, @Param("tenantId") Long tenantId);
}
