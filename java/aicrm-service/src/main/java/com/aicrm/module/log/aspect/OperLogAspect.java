package com.aicrm.module.log.aspect;

import com.aicrm.common.auth.UserContext;
import com.aicrm.common.context.TenantContext;
import com.aicrm.common.util.IpUtil;
import com.aicrm.module.log.annotation.OperLog;
import com.aicrm.module.log.service.LogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * 操作日志切面：自动记录标注 @OperLog 的接口调用（成功/失败、耗时、IP、参数脱敏）
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperLogAspect {

    /** 敏感字段脱敏：password/passwordHash 等值替换为 *** */
    private static final Pattern SENSITIVE_FIELD =
            Pattern.compile("(\"(?:password|passwordHash|secret|token)\"\\s*:\\s*)\"[^\"]*\"");

    private static final int MAX_PARAMS_LENGTH = 2000;
    private static final int MAX_ERROR_LENGTH = 1000;

    private final LogService logService;
    private final ObjectMapper objectMapper;

    @Around("@annotation(operLog)")
    public Object around(ProceedingJoinPoint joinPoint, OperLog operLog) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            saveLog(joinPoint, operLog, 1, null, System.currentTimeMillis() - start);
            return result;
        } catch (Throwable e) {
            saveLog(joinPoint, operLog, 0, e.getMessage(), System.currentTimeMillis() - start);
            throw e;
        }
    }

    private void saveLog(ProceedingJoinPoint joinPoint, OperLog annotation, int result,
                         String errorMsg, long durationMs) {
        try {
            com.aicrm.module.log.entity.OperLog record = new com.aicrm.module.log.entity.OperLog();
            record.setTenantId(TenantContext.getTenantId());
            record.setUserId(UserContext.getUserId());
            record.setModule(annotation.module());
            record.setOperation(annotation.operation());
            record.setMethod(joinPoint.getSignature().toShortString());
            record.setResult(result);
            record.setErrorMsg(truncate(errorMsg, MAX_ERROR_LENGTH));
            record.setDurationMs(durationMs);
            record.setRequestParams(serializeArgs(joinPoint.getArgs()));

            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                record.setRequestUrl(request.getRequestURI());
                record.setHttpMethod(request.getMethod());
                record.setIp(IpUtil.getIp(request));
            }
            logService.recordOperLog(record);
        } catch (Exception e) {
            log.warn("记录操作日志失败: {}", e.getMessage());
        }
    }

    /** 序列化请求参数并脱敏，超长截断 */
    private String serializeArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return null;
        }
        try {
            String json = objectMapper.writeValueAsString(Arrays.asList(args));
            json = SENSITIVE_FIELD.matcher(json).replaceAll("$1\"***\"");
            return truncate(json, MAX_PARAMS_LENGTH);
        } catch (Exception e) {
            return null;
        }
    }

    private String truncate(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        return value.length() > maxLength ? value.substring(0, maxLength) : value;
    }
}
