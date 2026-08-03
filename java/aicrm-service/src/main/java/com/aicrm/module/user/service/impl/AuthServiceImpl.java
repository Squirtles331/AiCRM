package com.aicrm.module.user.service.impl;

import com.aicrm.common.ResultCode;
import com.aicrm.common.exception.BusinessException;
import com.aicrm.common.util.JwtUtil;
import com.aicrm.common.util.PasswordUtil;
import com.aicrm.module.system.entity.Role;
import com.aicrm.module.system.mapper.MenuMapper;
import com.aicrm.module.system.mapper.RoleMapper;
import com.aicrm.module.tenant.entity.Tenant;
import com.aicrm.module.tenant.mapper.TenantMapper;
import com.aicrm.module.user.dto.LoginRequest;
import com.aicrm.module.user.dto.LoginResponse;
import com.aicrm.module.user.entity.User;
import com.aicrm.module.user.mapper.UserMapper;
import com.aicrm.module.user.service.AuthService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 认证服务实现：租户可用性校验 → 账号密码校验 → 汇总角色/权限 → 签发 JWT
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final TenantMapper tenantMapper;
    private final RoleMapper roleMapper;
    private final MenuMapper menuMapper;
    private final JwtUtil jwtUtil;

    @Override
    public LoginResponse login(LoginRequest request) {
        if (request.getTenantId() == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "tenantId 不能为空");
        }
        // 租户状态与到期控制：停用/过期租户禁止登录
        Tenant tenant = tenantMapper.selectById(request.getTenantId());
        if (tenant == null) {
            throw new BusinessException(ResultCode.TENANT_NOT_FOUND);
        }
        if (tenant.getStatus() != null && tenant.getStatus() == 0) {
            throw new BusinessException(ResultCode.TENANT_DISABLED);
        }
        if (tenant.getExpireAt() != null && tenant.getExpireAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ResultCode.TENANT_EXPIRED);
        }

        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getTenantId, request.getTenantId())
                .eq(User::getMobile, request.getMobile()));
        // 统一提示，避免暴露账号是否存在
        if (user == null || !PasswordUtil.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "账号或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "账号已禁用，请联系管理员");
        }
        // 记录最近活跃
        User update = new User();
        update.setId(user.getId());
        update.setLastActiveAt(LocalDateTime.now());
        userMapper.updateById(update);

        // 汇总角色与按钮权限：优先用户-角色关联表，未配置时回退主角色（users.role_code，兼容旧数据）
        List<String> roleCodes = roleMapper.selectRolesByUserId(user.getId(), user.getTenantId())
                .stream().map(Role::getCode).distinct().toList();
        if (roleCodes.isEmpty()) {
            roleCodes = user.getRoleCode() == null ? List.of() : List.of(user.getRoleCode());
        }
        List<String> perms = menuMapper.selectPermsByRoleCodes(user.getTenantId(), roleCodes);

        String token = jwtUtil.createToken(user.getId(), user.getTenantId(), roleCodes, perms);
        log.info("用户登录成功 userId={}, tenantId={}, roles={}", user.getId(), user.getTenantId(), roleCodes);
        return new LoginResponse(token, user.getId(), user.getTenantId(),
                roleCodes.isEmpty() ? null : roleCodes.get(0), roleCodes, perms, user.getName());
    }
}
