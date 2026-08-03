package com.aicrm.module.system.service.impl;

import com.aicrm.common.ResultCode;
import com.aicrm.common.auth.UserContext;
import com.aicrm.common.exception.BusinessException;
import com.aicrm.module.system.entity.Menu;
import com.aicrm.module.system.mapper.MenuMapper;
import com.aicrm.module.system.service.MenuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 菜单/权限服务实现
 */
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuMapper menuMapper;

    @Override
    public List<Menu> listTree() {
        List<Menu> menus = menuMapper.selectList(new LambdaQueryWrapper<Menu>()
                .orderByAsc(Menu::getSort).orderByAsc(Menu::getId));
        return buildTree(menus);
    }

    @Override
    public Menu createMenu(Menu menu) {
        if (!StringUtils.hasText(menu.getMenuName())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "菜单名称不能为空");
        }
        if (menu.getMenuType() == null) {
            menu.setMenuType("menu");
        }
        if (menu.getParentId() == null) {
            menu.setParentId(0L);
        }
        if (menu.getSort() == null) {
            menu.setSort(0);
        }
        if (menu.getVisible() == null) {
            menu.setVisible(1);
        }
        if (menu.getStatus() == null) {
            menu.setStatus(1);
        }
        menuMapper.insert(menu);
        return menu;
    }

    @Override
    public Menu updateMenu(Menu menu) {
        if (menu.getId() == null || menuMapper.selectById(menu.getId()) == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "菜单不存在");
        }
        if (menu.getId().equals(menu.getParentId())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "父菜单不能是自己");
        }
        menuMapper.updateById(menu);
        return menuMapper.selectById(menu.getId());
    }

    @Override
    public void deleteMenu(Long id) {
        Long childCount = menuMapper.selectCount(new LambdaQueryWrapper<Menu>().eq(Menu::getParentId, id));
        if (childCount != null && childCount > 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "存在子菜单，请先删除子菜单");
        }
        menuMapper.deleteById(id);
    }

    @Override
    public List<Menu> getRoutersByUser() {
        List<String> roleCodes = UserContext.getRoleCodes();
        List<Menu> menus;
        if (roleCodes != null && roleCodes.contains("admin")) {
            // admin 角色兜底：返回全部可见菜单
            menus = menuMapper.selectList(new LambdaQueryWrapper<Menu>()
                    .eq(Menu::getVisible, 1)
                    .eq(Menu::getStatus, 1)
                    .in(Menu::getMenuType, List.of("dir", "menu"))
                    .orderByAsc(Menu::getSort).orderByAsc(Menu::getId));
        } else {
            menus = menuMapper.selectMenusByRoleCodes(UserContext.getTenantId(),
                    roleCodes == null ? List.of() : roleCodes);
        }
        return buildTree(menus);
    }

    /** 组装树：parentId=0 为根，按 sort 排序 */
    private List<Menu> buildTree(List<Menu> menus) {
        Map<Long, Menu> menuMap = new HashMap<>();
        for (Menu menu : menus) {
            menu.setChildren(new ArrayList<>());
            menuMap.put(menu.getId(), menu);
        }
        List<Menu> roots = new ArrayList<>();
        for (Menu menu : menus) {
            Menu parent = menuMap.get(menu.getParentId());
            if (parent != null) {
                parent.getChildren().add(menu);
            } else {
                roots.add(menu);
            }
        }
        return roots;
    }
}
