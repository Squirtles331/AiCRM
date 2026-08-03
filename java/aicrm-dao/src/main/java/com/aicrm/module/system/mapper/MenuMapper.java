package com.aicrm.module.system.mapper;

import com.aicrm.module.system.entity.Menu;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 菜单 Mapper
 */
public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * 查询角色集合拥有的按钮权限码（sys_menu 无 tenant_id，禁用租户拦截后显式按租户过滤）
     */
    @InterceptorIgnore(tenantLine = "true")
    @Select({
            "<script>",
            "SELECT DISTINCT m.perms FROM sys_menu m",
            "JOIN sys_role_menu rm ON rm.menu_id = m.id AND rm.deleted = 0",
            "JOIN sys_role r ON r.id = rm.role_id AND r.deleted = 0 AND r.status = 1",
            "WHERE rm.tenant_id = #{tenantId} AND m.deleted = 0 AND m.status = 1",
            "AND m.perms IS NOT NULL AND m.perms != ''",
            "AND r.code IN",
            "<foreach collection='roleCodes' item='code' open='(' separator=',' close=')'>#{code}</foreach>",
            "</script>"
    })
    List<String> selectPermsByRoleCodes(@Param("tenantId") Long tenantId,
                                        @Param("roleCodes") List<String> roleCodes);

    /**
     * 查询角色集合可见的菜单（含目录/菜单，不含按钮），用于构建当前用户路由
     */
    @InterceptorIgnore(tenantLine = "true")
    @Select({
            "<script>",
            "SELECT DISTINCT m.* FROM sys_menu m",
            "JOIN sys_role_menu rm ON rm.menu_id = m.id AND rm.deleted = 0",
            "JOIN sys_role r ON r.id = rm.role_id AND r.deleted = 0 AND r.status = 1",
            "WHERE rm.tenant_id = #{tenantId} AND m.deleted = 0 AND m.status = 1 AND m.visible = 1",
            "AND m.menu_type IN ('dir', 'menu')",
            "AND r.code IN",
            "<foreach collection='roleCodes' item='code' open='(' separator=',' close=')'>#{code}</foreach>",
            "</script>"
    })
    List<Menu> selectMenusByRoleCodes(@Param("tenantId") Long tenantId,
                                      @Param("roleCodes") List<String> roleCodes);
}
