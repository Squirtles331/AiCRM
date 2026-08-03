package com.aicrm.module.openapi.config;

import com.aicrm.module.openapi.annotation.MockResponse;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import java.util.Map;

/**
 * Mock 响应示例注入（阶段 2.3）
 * <p>扫描 Controller 方法上的 {@link MockResponse} 注解，将示例 JSON 注入
 * 该接口 200 响应的 example 中，前端可在文档中预览 Mock 数据。
 */
@Component
public class MockResponseOperationCustomizer implements OperationCustomizer {

    @Override
    public io.swagger.v3.oas.models.Operation customize(io.swagger.v3.oas.models.Operation operation,
                                                        HandlerMethod handlerMethod) {
        MockResponse mock = handlerMethod.getMethodAnnotation(MockResponse.class);
        if (mock == null || operation.getResponses() == null) {
            return operation;
        }
        String json = mock.value();
        if (json == null || json.isBlank()) {
            return operation;
        }
        String description = mock.note() == null || mock.note().isBlank()
                ? "Mock 响应（接口未开发完成，前端可基于此示例开发）"
                : "Mock 响应：" + mock.note();
        operation.getResponses().addApiResponse("200",
                new ApiResponse()
                        .description(description)
                        .content(new Content().addMediaType("application/json",
                                new MediaType().examples(Map.of("mock",
                                        new Example().summary("Mock 示例").value(json))))));
        return operation;
    }
}
