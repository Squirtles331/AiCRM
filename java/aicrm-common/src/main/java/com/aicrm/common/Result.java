package com.aicrm.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一 API 返回结构
 */
@Data
@Schema(description = "统一 API 返回结构")
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 业务码：200 成功，其余为失败 */
    @Schema(description = "业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）", example = "200")
    private Integer code;

    /** 提示信息 */
    @Schema(description = "提示信息", example = "操作成功")
    private String message;

    /** 数据 */
    @Schema(description = "业务数据")
    private T data;

    public static <T> Result<T> ok() {
        return build(ResultCode.SUCCESS, null);
    }

    public static <T> Result<T> ok(T data) {
        return build(ResultCode.SUCCESS, data);
    }

    public static <T> Result<T> ok(T data, String message) {
        Result<T> r = build(ResultCode.SUCCESS, data);
        r.setMessage(message);
        return r;
    }

    public static <T> Result<T> fail(ResultCode code) {
        return build(code, null);
    }

    public static <T> Result<T> fail(ResultCode code, String message) {
        Result<T> r = build(code, null);
        r.setMessage(message);
        return r;
    }

    public static <T> Result<T> fail(int code, String message) {
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setMessage(message);
        return r;
    }

    private static <T> Result<T> build(ResultCode code, T data) {
        Result<T> r = new Result<>();
        r.setCode(code.getCode());
        r.setMessage(code.getMessage());
        r.setData(data);
        return r;
    }
}
