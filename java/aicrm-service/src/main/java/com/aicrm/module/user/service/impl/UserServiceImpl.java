package com.aicrm.module.user.service.impl;

import com.aicrm.common.PageResult;
import com.aicrm.common.ResultCode;
import com.aicrm.common.context.TenantContext;
import com.aicrm.common.exception.BusinessException;
import com.aicrm.common.util.PasswordUtil;
import com.aicrm.module.system.entity.UserRole;
import com.aicrm.module.system.mapper.UserRoleMapper;
import com.aicrm.module.user.entity.User;
import com.aicrm.module.user.mapper.UserMapper;
import com.aicrm.module.user.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 用户服务实现
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserRoleMapper userRoleMapper;

    public UserServiceImpl(UserRoleMapper userRoleMapper) {
        this.userRoleMapper = userRoleMapper;
    }

    @Override
    public PageResult<User> pageUsers(Long tenantId, long page, long size) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(tenantId != null, User::getTenantId, tenantId)
                .orderByDesc(User::getId);
        return PageResult.of(this.page(new Page<>(page, size), wrapper));
    }

    @Override
    public User createUser(User user) {
        if (user.getTenantId() == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "tenantId 不能为空");
        }
        if (!StringUtils.hasText(user.getMobile())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "手机号不能为空");
        }
        if (!StringUtils.hasText(user.getPasswordHash())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "密码不能为空");
        }
        if (user.getRoleCode() == null) {
            user.setRoleCode("sales");
        }
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        this.save(user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (user.getId() == null || getById(user.getId()) == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        user.setPasswordHash(null); // 密码通过 resetPassword 单独管理
        this.updateById(user);
        return getById(user.getId());
    }

    @Override
    public void deleteUser(Long id) {
        if (getById(id) == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        this.removeById(id);
    }

    @Override
    public void resetPassword(Long id, String newPassword) {
        if (getById(id) == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        if (!StringUtils.hasText(newPassword) || newPassword.length() < 6) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "新密码长度不能少于 6 位");
        }
        User update = new User();
        update.setId(id);
        update.setPasswordHash(PasswordUtil.hash(newPassword));
        this.updateById(update);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        if (getById(id) == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "status 仅支持 0/1");
        }
        User update = new User();
        update.setId(id);
        update.setStatus(status);
        this.updateById(update);
    }

    @Override
    public List<Long> getRoleIds(Long userId) {
        List<UserRole> userRoles = userRoleMapper.selectList(new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, userId));
        return userRoles.stream().map(UserRole::getRoleId).toList();
    }

    @Override
    public void setUserRoles(Long userId, List<Long> roleIds) {
        if (getById(userId) == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        // 全量覆盖：旧关联逻辑删除后重新插入
        userRoleMapper.update(null, new LambdaUpdateWrapper<UserRole>()
                .eq(UserRole::getUserId, userId)
                .set(UserRole::getDeleted, 1));
        if (!CollectionUtils.isEmpty(roleIds)) {
            Long tenantId = TenantContext.getTenantId();
            for (Long roleId : roleIds) {
                UserRole userRole = new UserRole();
                userRole.setTenantId(tenantId);
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRoleMapper.insert(userRole);
            }
        }
    }
}
