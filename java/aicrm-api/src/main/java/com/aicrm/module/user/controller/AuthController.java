package com.aicrm.module.user.controller;

import com.aicrm.common.Result;
import com.aicrm.common.exception.BusinessException;
import com.aicrm.common.util.IpUtil;
import com.aicrm.module.log.annotation.OperLog;
import com.aicrm.module.log.service.LogService;
import com.aicrm.module.user.dto.LoginRequest;
import com.aicrm.module.user.dto.LoginResponse;
import com.aicrm.module.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证接口
 */
@Tag(name = "认证")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final LogService logService;

    @Operation(summary = "登录（签发 JWT）", description = "公开接口，无需登录；成功返回 JWT 令牌")
    @ApiResponse(responseCode = "400", description = "账号或密码错误/账号已禁用/参数缺失")
    @ApiResponse(responseCode = "1001", description = "租户不存在")
    @ApiResponse(responseCode = "1002", description = "租户已停用")
    @ApiResponse(responseCode = "1003", description = "租户已到期")
    @OperLog(module = "认证", operation = "用户登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request,
                                       HttpServletRequest servletRequest) {
        String ip = IpUtil.getIp(servletRequest);
        String userAgent = servletRequest.getHeader("User-Agent");
        try {
            LoginResponse response = authService.login(request);
            logService.recordLoginLog(request.getTenantId(), response.getUserId(),
                    request.getMobile(), ip, userAgent, 1, "登录成功");
            return Result.ok(response);
        } catch (BusinessException e) {
            logService.recordLoginLog(request.getTenantId(), null,
                    request.getMobile(), ip, userAgent, 0, e.getMessage());
            throw e;
        }
    }
}
