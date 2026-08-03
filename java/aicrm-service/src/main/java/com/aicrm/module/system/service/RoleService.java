package com.aicrm.module.system.service;

import com.aicrm.common.PageResult;
import com.aicrm.module.system.entity.Role;

import java.util.List;

/**
 * 角色服务（租户内）
 */
public interface RoleService {

    /**
     * 分页查询角色
     */
    PageResult<Role> pageRoles(String keyword, long page, long size);

    /**
     * 全部启用角色（下拉用）
     */
    List<Role> listEnabled();

    /**
     * 角色详情
     */
    Role detail(Long id);

    /**
     * 创建角色
     */
    Role createRole(Role role);

    /**
     * 更新角色
     */
    Role updateRole(Role role);

    /**
     * 删除角色（逻辑删除）
     */
    void deleteRole(Long id);

    /**
     * 启用/停用角色
     */
    void updateStatus(Long id, Integer status);

    /**
     * 查询角色已分配的菜单 ID 列表
     */
    List<Long> listMenuIdsByRole(Long roleId);

    /**
     * 重新分配角色-菜单关联（全量覆盖）
     */
    void setRoleMenus(Long roleId, List<Long> menuIds);
}
