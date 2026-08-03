package com.aicrm.module.openapi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mock 响应注解（阶段 2.3）
 * <p>针对未开发完成的接口，标注示例返回 JSON。文档生成时自动注入到接口的
 * 200 响应示例中，前端无需等待接口完成即可基于 Mock 数据渲染页面。
 * <p>示例：
 * <pre>
 * {@code @MockResponse("{\"code\":200,\"message\":\"操作成功\",\"data\":{}}")}
 * </pre>
 * 接口完成后移除该注解即可。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MockResponse {

    /**
     * 示例返回 JSON 字符串（完整 Result 结构：{code, message, data}）
     */
    String value() default "";

    /**
     * 使用说明/标注原因
     */
    String note() default "";
}
