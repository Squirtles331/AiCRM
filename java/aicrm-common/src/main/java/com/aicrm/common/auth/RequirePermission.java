package com.aicrm.common.auth;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限注解：标注在接口方法上，要求当前用户满足任一角色码 或 任一按钮权限码
 * <p>
 * value() 角色码：与 sys_role.code 对应（admin/sales/supervisor 等），满足其一即可。
 * perms() 按钮权限码：与 sys_menu.perms 对应（如 user:add），满足其一即可。
 * 两者均为空时表示仅需登录即可访问；未标注注解的接口默认需登录。
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {

    /**
     * 允许访问的角色码，满足其一即可；空数组表示不限制角色
     */
    String[] value() default {};

    /**
     * 允许访问的按钮权限码，满足其一即可；空数组表示不限制权限码
     */
    String[] perms() default {};
}
