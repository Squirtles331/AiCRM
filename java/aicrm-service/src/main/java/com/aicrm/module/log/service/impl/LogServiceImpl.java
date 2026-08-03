package com.aicrm.module.log.service.impl;

import com.aicrm.common.PageResult;
import com.aicrm.common.context.TenantContext;
import com.aicrm.module.log.entity.LoginLog;
import com.aicrm.module.log.entity.OperLog;
import com.aicrm.module.log.mapper.LoginLogMapper;
import com.aicrm.module.log.mapper.OperLogMapper;
import com.aicrm.module.log.service.LogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 日志与审计服务实现
 */
@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {

    private final OperLogMapper operLogMapper;
    private final LoginLogMapper loginLogMapper;

    @Override
    public PageResult<OperLog> pageOperLogs(String keyword, String module, Integer result, long page, long size) {
        LambdaQueryWrapper<OperLog> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(OperLog::getOperation, keyword)
                    .or().like(OperLog::getUserName, keyword)
                    .or().like(OperLog::getRequestUrl, keyword));
        }
        wrapper.eq(StringUtils.hasText(module), OperLog::getModule, module)
                .eq(result != null, OperLog::getResult, result)
                .orderByDesc(OperLog::getId);
        return PageResult.of(operLogMapper.selectPage(new Page<>(page, size), wrapper));
    }

    @Override
    public PageResult<LoginLog> pageLoginLogs(String mobile, Integer status, long page, long size) {
        LambdaQueryWrapper<LoginLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(mobile), LoginLog::getMobile, mobile)
                .eq(status != null, LoginLog::getStatus, status)
                .orderByDesc(LoginLog::getId);
        return PageResult.of(loginLogMapper.selectPage(new Page<>(page, size), wrapper));
    }

    @Override
    public void recordOperLog(OperLog operLog) {
        if (operLog.getTenantId() == null) {
            operLog.setTenantId(TenantContext.getTenantId());
        }
        operLogMapper.insert(operLog);
    }

    @Override
    public void recordLoginLog(Long tenantId, Long userId, String mobile, String ip, String userAgent,
                               Integer status, String message) {
        LoginLog loginLog = new LoginLog();
        loginLog.setTenantId(tenantId);
        loginLog.setUserId(userId);
        loginLog.setMobile(mobile);
        loginLog.setIp(ip);
        loginLog.setUserAgent(userAgent != null && userAgent.length() > 300
                ? userAgent.substring(0, 300) : userAgent);
        loginLog.setStatus(status);
        loginLog.setMessage(message != null && message.length() > 500
                ? message.substring(0, 500) : message);
        loginLogMapper.insert(loginLog);
    }
}
