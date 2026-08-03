package com.aicrm.module.log.controller;

import com.aicrm.common.PageResult;
import com.aicrm.common.Result;
import com.aicrm.common.auth.RequirePermission;
import com.aicrm.module.log.entity.LoginLog;
import com.aicrm.module.log.entity.OperLog;
import com.aicrm.module.log.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 日志查询接口
 */
@Tag(name = "日志管理")
@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    @Operation(summary = "操作日志分页查询", description = "需权限 log:list")
    @RequirePermission(perms = "log:list")
    @GetMapping("/oper")
    public Result<PageResult<OperLog>> pageOperLogs(
            @Parameter(description = "关键字（操作人姓名/操作内容/请求地址模糊匹配）") @RequestParam(required = false) String keyword,
            @Parameter(description = "业务模块") @RequestParam(required = false) String module,
            @Parameter(description = "执行结果：1 成功 / 0 失败") @RequestParam(required = false) Integer result,
            @Parameter(description = "页码，默认 1") @RequestParam(defaultValue = "1") long page,
            @Parameter(description = "每页条数，默认 20") @RequestParam(defaultValue = "20") long size) {
        return Result.ok(logService.pageOperLogs(keyword, module, result, page, size));
    }

    @Operation(summary = "登录日志分页查询", description = "需权限 log:list")
    @RequirePermission(perms = "log:list")
    @GetMapping("/login")
    public Result<PageResult<LoginLog>> pageLoginLogs(
            @Parameter(description = "登录手机号") @RequestParam(required = false) String mobile,
            @Parameter(description = "登录结果：1 成功 / 0 失败") @RequestParam(required = false) Integer status,
            @Parameter(description = "页码，默认 1") @RequestParam(defaultValue = "1") long page,
            @Parameter(description = "每页条数，默认 20") @RequestParam(defaultValue = "20") long size) {
        return Result.ok(logService.pageLoginLogs(mobile, status, page, size));
    }
}
