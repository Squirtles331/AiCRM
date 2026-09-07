package com.aicrm.web.api;

import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.error.ErrorCode;
import com.aicrm.kernel.security.TraceContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Maps shared domain failures, including failures raised before controller invocation. */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class V1DomainExceptionHandler {
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiResponse<Void>> domain(DomainException exception) {
        ErrorCode code = exception.errorCode();
        return ResponseEntity.status(code.httpStatus())
                .body(ApiResponse.failure(code.value(), exception.getMessage(), TraceContext.get()));
    }
}
