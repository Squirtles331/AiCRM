package com.aicrm.module.tenant.service.impl;

import com.aicrm.common.PageResult;
import com.aicrm.common.ResultCode;
import com.aicrm.common.context.TenantContext;
import com.aicrm.common.exception.BusinessException;
import com.aicrm.common.util.PasswordUtil;
import com.aicrm.module.tenant.dto.TenantCreateRequest;
import com.aicrm.module.tenant.entity.Plan;
import com.aicrm.module.tenant.entity.Tenant;
import com.aicrm.module.tenant.mapper.PlanMapper;
import com.aicrm.module.tenant.mapper.TenantMapper;
import com.aicrm.module.tenant.service.TenantService;
import com.aicrm.module.system.entity.Menu;
import com.aicrm.module.system.entity.Role;
import com.aicrm.module.system.entity.RoleMenu;
import com.aicrm.module.system.entity.UserRole;
import com.aicrm.module.system.mapper.MenuMapper;
import com.aicrm.module.system.mapper.RoleMapper;
import com.aicrm.module.system.mapper.RoleMenuMapper;
import com.aicrm.module.system.mapper.UserRoleMapper;
import com.aicrm.module.user.entity.User;
import com.aicrm.module.user.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 租户管理服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenantServiceImpl extends ServiceImpl<TenantMapper, Tenant> implements TenantService {

    /** 租户初始化管理员默认密码（首次登录后应强制修改） */
    private static final String DEFAULT_ADMIN_PASSWORD = "Aicrm@123456";

    private final PlanMapper planMapper;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final MenuMapper menuMapper;
    private final RoleMenuMapper roleMenuMapper;
    private final UserRoleMapper userRoleMapper;

    @Override
    public PageResult<Tenant> pageTenants(String keyword, String planCode, Integer status, long page, long size) {
        LambdaQueryWrapper<Tenant> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Tenant::getName, keyword)
                    .or().like(Tenant::getContactName, keyword)
                    .or().like(Tenant::getContactMobile, keyword));
        }
        wrapper.eq(StringUtils.hasText(planCode), Tenant::getPlanCode, planCode)
                .eq(status != null, Tenant::getStatus, status)
                .orderByDesc(Tenant::getId);
        return PageResult.of(this.page(new Page<>(page, size), wrapper));
    }

    @Override
    public Tenant detail(Long id) {
        Tenant tenant = getById(id);
        if (tenant == null) {
            throw new BusinessException(ResultCode.TENANT_NOT_FOUND);
        }
        return tenant;
    }

    @Override
    public Tenant createTenant(TenantCreateRequest request) {
        Plan plan = resolvePlan(request.getPlanCode());
        Tenant tenant = new Tenant();
        tenant.setName(request.getName());
        tenant.setPlanCode(plan.getCode());
        tenant.setSeatCount(request.getSeatCount() != null ? request.getSeatCount() : plan.getSeatCount());
        tenant.setExpireAt(request.getExpireAt());
        tenant.setContactName(request.getContactName());
        tenant.setContactMobile(request.getContactMobile());
        tenant.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        this.save(tenant);

        initTenantData(tenant, request.getAdminMobile(), request.getAdminPassword());
        log.info("创建租户 id={}, name={}, plan={}", tenant.getId(), tenant.getName(), plan.getCode());
        return tenant;
    }

    @Override
    public Tenant updateTenant(Tenant tenant) {
        if (tenant.getId() == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "id 不能为空");
        }
        Tenant exists = getById(tenant.getId());
        if (exists == null) {
            throw new BusinessException(ResultCode.TENANT_NOT_FOUND);
        }
        // 切换套餐：校验套餐有效，未显式指定坐席/额度时按新套餐默认带出
        if (StringUtils.hasText(tenant.getPlanCode())) {
            Plan plan = resolvePlan(tenant.getPlanCode());
            tenant.setPlanCode(plan.getCode());
            if (tenant.getSeatCount() == null) {
                tenant.setSeatCount(plan.getSeatCount());
            }
        }
        this.updateById(tenant);
        return getById(tenant.getId());
    }

    @Override
    public void deleteTenant(Long id) {
        if (getById(id) == null) {
            throw new BusinessException(ResultCode.TENANT_NOT_FOUND);
        }
        this.removeById(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        if (getById(id) == null) {
            throw new BusinessException(ResultCode.TENANT_NOT_FOUND);
        }
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "status 仅支持 0（停用）/1（启用）");
        }
        Tenant update = new Tenant();
        update.setId(id);
        update.setStatus(status);
        this.updateById(update);
    }

    @Override
    public void checkTenantAvailable(Long tenantId) {
        if (tenantId == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "tenantId 不能为空");
        }
        Tenant tenant = getById(tenantId);
        if (tenant == null) {
            throw new BusinessException(ResultCode.TENANT_NOT_FOUND);
        }
        if (tenant.getStatus() != null && tenant.getStatus() == 0) {
            throw new BusinessException(ResultCode.TENANT_DISABLED);
        }
        if (tenant.getExpireAt() != null && tenant.getExpireAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ResultCode.TENANT_EXPIRED);
        }
    }

    /** 校验套餐存在且上架，返回套餐定义 */
    private Plan resolvePlan(String planCode) {
        String code = StringUtils.hasText(planCode) ? planCode : "starter";
        Plan plan = planMapper.selectOne(new LambdaQueryWrapper<Plan>().eq(Plan::getCode, code));
        if (plan == null) {
            throw new BusinessException(ResultCode.PLAN_NOT_FOUND);
        }
        if (plan.getStatus() != null && plan.getStatus() == 0) {
            throw new BusinessException(ResultCode.PLAN_DISABLED);
        }
        return plan;
    }

    /**
     * 租户初始化数据：默认管理员账号 + 默认角色（admin/sales/supervisor）+ admin 绑定全部菜单
     * <p>后续 2.3 字典/参数落地后，在此扩展默认字典、系统参数等初始化数据。
     */
    private void initTenantData(Tenant tenant, String adminMobile, String adminPassword) {
        if (adminMobile == null || adminMobile.isBlank()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "管理员手机号不能为空");
        }
        User admin = new User();
        admin.setTenantId(tenant.getId());
        admin.setName("系统管理员");
        admin.setMobile(adminMobile);
        admin.setPasswordHash(PasswordUtil.hash(
                StringUtils.hasText(adminPassword) ? adminPassword : DEFAULT_ADMIN_PASSWORD));
        admin.setRoleCode("admin");
        admin.setStatus(1);

        // 用户表受租户拦截器控制：切换上下文到新租户后写入，避免沿用当前请求租户
        Long prev = TenantContext.getTenantId();
        try {
            TenantContext.setTenantId(tenant.getId());
            userMapper.insert(admin);

            // 默认角色
            Role adminRole = insertRole(tenant.getId(), "admin", "超级管理员", "拥有全部权限，由平台初始化");
            insertRole(tenant.getId(), "sales", "销售", "一线销售默认角色");
            insertRole(tenant.getId(), "supervisor", "主管", "销售主管");

            // 管理员绑定 admin 角色
            UserRole adminUserRole = new UserRole();
            adminUserRole.setTenantId(tenant.getId());
            adminUserRole.setUserId(admin.getId());
            adminUserRole.setRoleId(adminRole.getId());
            userRoleMapper.insert(adminUserRole);

            // admin 角色绑定全部启用菜单
            List<Menu> menus = menuMapper.selectList(new LambdaQueryWrapper<Menu>().eq(Menu::getStatus, 1));
            for (Menu menu : menus) {
                RoleMenu roleMenu = new RoleMenu();
                roleMenu.setTenantId(tenant.getId());
                roleMenu.setRoleId(adminRole.getId());
                roleMenu.setMenuId(menu.getId());
                roleMenuMapper.insert(roleMenu);
            }
        } finally {
            if (prev == null) {
                TenantContext.clear();
            } else {
                TenantContext.setTenantId(prev);
            }
        }
    }

    /** 插入角色（租户内），返回角色实体 */
    private Role insertRole(Long tenantId, String code, String name, String description) {
        Role role = new Role();
        role.setTenantId(tenantId);
        role.setCode(code);
        role.setName(name);
        role.setDescription(description);
        role.setStatus(1);
        roleMapper.insert(role);
        return role;
    }
}
