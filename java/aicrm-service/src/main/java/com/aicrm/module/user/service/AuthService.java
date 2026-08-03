package com.aicrm.module.user.service;

import com.aicrm.module.user.dto.LoginRequest;
import com.aicrm.module.user.dto.LoginResponse;

/**
 * 认证服务
 */
public interface AuthService {

    /**
     * 登录：校验账号密码并签发 JWT
     *
     * @param request 登录请求（租户/手机号/密码）
     * @return 令牌与用户信息
     */
    LoginResponse login(LoginRequest request);
}
