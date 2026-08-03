package com.aicrm.module.system.service;

import com.aicrm.module.system.entity.Menu;

import java.util.List;

/**
 * 菜单/权限服务（平台级）
 */
public interface MenuService {

    /**
     * 全量菜单树（含按钮，平台管理用）
     */
    List<Menu> listTree();

    /**
     * 创建菜单
     */
    Menu createMenu(Menu menu);

    /**
     * 更新菜单
     */
    Menu updateMenu(Menu menu);

    /**
     * 删除菜单（有子菜单时禁止删除）
     */
    void deleteMenu(Long id);

    /**
     * 当前登录用户可见菜单树（dir/menu，按用户角色关联过滤）
     */
    List<Menu> getRoutersByUser();
}
