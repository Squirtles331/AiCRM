package com.aicrm.module.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 登录请求
 */
@Data
@Schema(description = "登录请求")
public class LoginRequest {

    @NotNull(message = "租户 ID 不能为空")
    @Schema(description = "租户 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long tenantId;

    @NotBlank(message = "手机号不能为空")
    @Schema(description = "手机号（登录账号）", requiredMode = Schema.RequiredMode.REQUIRED, example = "13800138000")
    private String mobile;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "登录密码（明文）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
