package com.aicrm.module.system.service.impl;

import com.aicrm.common.PageResult;
import com.aicrm.common.ResultCode;
import com.aicrm.common.context.TenantContext;
import com.aicrm.common.exception.BusinessException;
import com.aicrm.module.system.entity.Role;
import com.aicrm.module.system.entity.RoleMenu;
import com.aicrm.module.system.mapper.RoleMapper;
import com.aicrm.module.system.mapper.RoleMenuMapper;
import com.aicrm.module.system.service.RoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 角色服务实现
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    private final RoleMenuMapper roleMenuMapper;

    @Override
    public PageResult<Role> pageRoles(String keyword, long page, long size) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Role::getName, keyword).or().like(Role::getCode, keyword));
        }
        wrapper.orderByAsc(Role::getId);
        return PageResult.of(this.page(new Page<>(page, size), wrapper));
    }

    @Override
    public List<Role> listEnabled() {
        return this.list(new LambdaQueryWrapper<Role>()
                .eq(Role::getStatus, 1)
                .orderByAsc(Role::getId));
    }

    @Override
    public Role detail(Long id) {
        Role role = getById(id);
        if (role == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "角色不存在");
        }
        return role;
    }

    @Override
    public Role createRole(Role role) {
        if (!StringUtils.hasText(role.getCode()) || !StringUtils.hasText(role.getName())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "角色编码与名称不能为空");
        }
        Long exist = this.count(new LambdaQueryWrapper<Role>().eq(Role::getCode, role.getCode()));
        if (exist != null && exist > 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "角色编码已存在");
        }
        role.setTenantId(TenantContext.getTenantId());
        if (role.getStatus() == null) {
            role.setStatus(1);
        }
        this.save(role);
        return role;
    }

    @Override
    public Role updateRole(Role role) {
        if (role.getId() == null || getById(role.getId()) == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "角色不存在");
        }
        this.updateById(role);
        return getById(role.getId());
    }

    @Override
    public void deleteRole(Long id) {
        if (getById(id) == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "角色不存在");
        }
        this.removeById(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        if (getById(id) == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "角色不存在");
        }
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "status 仅支持 0/1");
        }
        Role update = new Role();
        update.setId(id);
        update.setStatus(status);
        this.updateById(update);
    }

    @Override
    public List<Long> listMenuIdsByRole(Long roleId) {
        List<RoleMenu> roleMenus = roleMenuMapper.selectList(new LambdaQueryWrapper<RoleMenu>()
                .eq(RoleMenu::getRoleId, roleId));
        return roleMenus.stream().map(RoleMenu::getMenuId).toList();
    }

    @Override
    public void setRoleMenus(Long roleId, List<Long> menuIds) {
        if (getById(roleId) == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "角色不存在");
        }
        // 全量覆盖：旧关联逻辑删除后重新插入
        roleMenuMapper.update(null, new LambdaUpdateWrapper<RoleMenu>()
                .eq(RoleMenu::getRoleId, roleId)
                .set(RoleMenu::getDeleted, 1));
        if (!CollectionUtils.isEmpty(menuIds)) {
            Long tenantId = TenantContext.getTenantId();
            for (Long menuId : menuIds) {
                RoleMenu roleMenu = new RoleMenu();
                roleMenu.setTenantId(tenantId);
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menuId);
                roleMenuMapper.insert(roleMenu);
            }
        }
    }
}
