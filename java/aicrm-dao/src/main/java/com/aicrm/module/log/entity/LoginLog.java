package com.aicrm.module.log.entity;

import com.aicrm.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 登录日志
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_login_log")
@Schema(description = "登录日志")
public class LoginLog extends BaseEntity {

    /** 租户 ID（登录失败时可能为空） */
    @Schema(description = "租户 ID（登录失败时可能为空）")
    private Long tenantId;

    /** 用户 ID（登录失败时为空） */
    @Schema(description = "用户 ID（登录失败时为空）")
    private Long userId;

    /** 登录手机号 */
    @Schema(description = "登录手机号", example = "13800000000")
    private String mobile;

    /** 来源 IP */
    @Schema(description = "来源 IP", example = "127.0.0.1")
    private String ip;

    /** User-Agent */
    @Schema(description = "User-Agent")
    private String userAgent;

    /** 结果：1 成功 / 0 失败 */
    @Schema(description = "结果：1 成功 / 0 失败", example = "1")
    private Integer status;

    /** 结果描述 */
    @Schema(description = "结果描述", example = "登录成功")
    private String message;
}
