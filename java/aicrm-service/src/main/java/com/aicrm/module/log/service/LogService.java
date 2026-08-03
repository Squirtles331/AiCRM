package com.aicrm.module.log.service;

import com.aicrm.common.PageResult;
import com.aicrm.module.log.entity.LoginLog;
import com.aicrm.module.log.entity.OperLog;

/**
 * 日志与审计服务
 */
public interface LogService {

    /**
     * 操作日志分页查询
     */
    PageResult<OperLog> pageOperLogs(String keyword, String module, Integer result, long page, long size);

    /**
     * 登录日志分页查询
     */
    PageResult<LoginLog> pageLoginLogs(String mobile, Integer status, long page, long size);

    /**
     * 记录操作日志（切面调用）
     */
    void recordOperLog(OperLog operLog);

    /**
     * 记录登录日志
     */
    void recordLoginLog(Long tenantId, Long userId, String mobile, String ip, String userAgent,
                        Integer status, String message);
}
