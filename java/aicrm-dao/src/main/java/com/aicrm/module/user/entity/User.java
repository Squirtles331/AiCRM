package com.aicrm.module.user.entity;

import com.aicrm.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 坐席/员工
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("users")
@Schema(description = "坐席/员工")
public class User extends BaseEntity {

    /** 租户 ID */
    @Schema(description = "租户 ID")
    private Long tenantId;

    /** 姓名 */
    @Schema(description = "姓名")
    private String name;

    /** 手机号 */
    @Schema(description = "手机号")
    private String mobile;

    /** 邮箱 */
    @Schema(description = "邮箱")
    private String email;

    /** 登录密码（BCrypt 哈希，不返回前端） */
    @com.fasterxml.jackson.annotation.JsonIgnore
    @Schema(description = "登录密码（BCrypt 哈希，不返回前端）")
    private String passwordHash;

    /** 角色编码：sales/supervisor/admin */
    @Schema(description = "角色编码：sales/supervisor/admin", example = "sales")
    private String roleCode;

    /** 状态：1 启用 / 0 停用 */
    @Schema(description = "状态：1启用/0停用", example = "1")
    private Integer status;

    /** 最后活跃时间 */
    @TableField("last_active_at")
    @Schema(description = "最后活跃时间")
    private LocalDateTime lastActiveAt;
}
