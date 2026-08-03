package com.aicrm.module.user.service;

import com.aicrm.common.PageResult;
import com.aicrm.module.user.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 用户服务
 */
public interface UserService extends IService<User> {

    /**
     * 分页查询租户下的坐席
     *
     * @param tenantId 租户 ID
     * @param page     页码（从 1 开始）
     * @param size     每页条数
     * @return 分页结果
     */
    PageResult<User> pageUsers(Long tenantId, long page, long size);

    /**
     * 创建坐席
     *
     * @param user 坐席信息（tenantId 必填）
     * @return 创建后的坐席
     */
    User createUser(User user);

    /**
     * 更新坐席信息（不含密码）
     */
    User updateUser(User user);

    /**
     * 删除坐席（逻辑删除）
     */
    void deleteUser(Long id);

    /**
     * 重置密码（BCrypt 哈希后落库）
     */
    void resetPassword(Long id, String newPassword);

    /**
     * 启用/停用坐席
     */
    void updateStatus(Long id, Integer status);

    /**
     * 查询用户已分配的角色 ID 列表
     */
    List<Long> getRoleIds(Long userId);

    /**
     * 重新分配用户角色（全量覆盖）
     */
    void setUserRoles(Long userId, List<Long> roleIds);
}
