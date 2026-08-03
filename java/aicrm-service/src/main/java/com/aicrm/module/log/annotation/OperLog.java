package com.aicrm.module.log.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解：标注在接口方法上，由切面自动记录操作日志（含成功/失败、耗时、IP）
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperLog {

    /** 业务模块，如 线索管理 */
    String module() default "";

    /** 操作内容，如 新建线索 */
    String operation() default "";
}
