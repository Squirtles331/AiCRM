package com.aicrm.kernel.error;

/** Stable application error codes used by the versioned API. */
public enum ErrorCode {
    VALIDATION_ERROR("VALIDATION_ERROR", 400),
    UNAUTHORIZED("UNAUTHORIZED", 401),
    FORBIDDEN("FORBIDDEN", 403),
    NOT_FOUND("NOT_FOUND", 404),
    CONFLICT("CONFLICT", 409),
    IDEMPOTENCY_IN_PROGRESS("IDEMPOTENCY_IN_PROGRESS", 409),
    LEAD_NOT_CLAIMABLE("LEAD_NOT_CLAIMABLE", 409),
    CUSTOMER_NOT_CLAIMABLE("CUSTOMER_NOT_CLAIMABLE", 409),
    OWNERSHIP_NOT_ALLOWED("OWNERSHIP_NOT_ALLOWED", 403),
    INTERNAL_ERROR("INTERNAL_ERROR", 500);

    private final String value;
    private final int httpStatus;

    ErrorCode(String value, int httpStatus) {
        this.value = value;
        this.httpStatus = httpStatus;
    }

    public String value() {
        return value;
    }

    public int httpStatus() {
        return httpStatus;
    }
}
