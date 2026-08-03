package com.aicrm.module.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 登录响应
 */
@Data
@AllArgsConstructor
@Schema(description = "登录响应")
public class LoginResponse {

    /** JWT 令牌 */
    @Schema(description = "JWT 令牌")
    private String token;

    @Schema(description = "用户 ID")
    private Long userId;

    @Schema(description = "租户 ID")
    private Long tenantId;

    /** 主角色码（角色集合第一个，兼容旧前端） */
    @Schema(description = "主角色码（角色集合第一个，兼容旧前端）：sales/supervisor/admin")
    private String roleCode;

    /** 角色码集合 */
    @Schema(description = "角色码集合：sales/supervisor/admin")
    private List<String> roles;

    /** 按钮权限码集合 */
    @Schema(description = "按钮权限码集合，如 user:add")
    private List<String> perms;

    @Schema(description = "姓名")
    private String name;
}
