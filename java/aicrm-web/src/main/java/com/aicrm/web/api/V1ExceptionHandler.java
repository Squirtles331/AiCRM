package com.aicrm.web.api;

import com.aicrm.kernel.error.ErrorCode;
import com.aicrm.kernel.security.TraceContext;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.aicrm.web")
public class V1ExceptionHandler {
    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class, IllegalArgumentException.class})
    public ResponseEntity<ApiResponse<Void>> validation(Exception exception) {
        return ResponseEntity.badRequest().body(ApiResponse.failure(ErrorCode.VALIDATION_ERROR.value(),
                "请求参数不合法", TraceContext.get()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> unexpected(Exception exception) {
        return ResponseEntity.internalServerError().body(ApiResponse.failure(ErrorCode.INTERNAL_ERROR.value(),
                "服务内部错误", TraceContext.get()));
    }
}
