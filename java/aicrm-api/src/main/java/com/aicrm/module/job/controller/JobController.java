package com.aicrm.module.job.controller;

import com.aicrm.common.PageResult;
import com.aicrm.common.Result;
import com.aicrm.common.auth.RequirePermission;
import com.aicrm.module.job.JobInfo;
import com.aicrm.module.job.JobScheduler;
import com.aicrm.module.job.entity.JobLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 定时任务接口
 */
@Tag(name = "定时任务")
@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobScheduler jobScheduler;

    @Operation(summary = "任务列表（含运行状态）", description = "需权限 job:list，返回全部已注册定时任务及其当前运行状态")
    @RequirePermission(perms = "job:list")
    @GetMapping
    public Result<List<JobInfo>> list() {
        return Result.ok(jobScheduler.listJobs());
    }

    @Operation(summary = "手动触发任务", description = "需权限 job:run，立即执行一次指定任务")
    @ApiResponse(responseCode = "404", description = "任务不存在")
    @RequirePermission(perms = "job:run")
    @PostMapping("/{code}/run")
    public Result<Void> run(
            @Parameter(description = "任务编码", required = true, example = "channelTokenRefresh") @PathVariable String code) {
        jobScheduler.runJob(code, "manual");
        return Result.ok();
    }

    @Operation(summary = "任务执行记录分页查询", description = "需权限 job:list")
    @RequirePermission(perms = "job:list")
    @GetMapping("/logs")
    public Result<PageResult<JobLog>> logs(
            @Parameter(description = "任务编码") @RequestParam(required = false) String jobCode,
            @Parameter(description = "执行结果：1 成功 / 0 失败") @RequestParam(required = false) Integer result,
            @Parameter(description = "页码，默认 1") @RequestParam(defaultValue = "1") long page,
            @Parameter(description = "每页条数，默认 20") @RequestParam(defaultValue = "20") long size) {
        return Result.ok(jobScheduler.pageLogs(jobCode, result, page, size));
    }
}
