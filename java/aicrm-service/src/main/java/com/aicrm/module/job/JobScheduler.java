package com.aicrm.module.job;

import com.aicrm.common.PageResult;
import com.aicrm.common.ResultCode;
import com.aicrm.common.exception.BusinessException;
import com.aicrm.module.job.entity.JobLog;
import com.aicrm.module.job.mapper.JobLogMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 任务调度器：基于 Spring Task 动态注册 cron 任务，统一记录执行日志（sys_job_log）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JobScheduler implements SchedulingConfigurer {

    private final JobRegistry jobRegistry;
    private final JobLogMapper jobLogMapper;

    /** 正在执行的任务编码（防重入） */
    private final Map<String, Boolean> runningJobs = new ConcurrentHashMap<>();

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        for (JobDefinition job : jobRegistry.list()) {
            if (job.isEnabled() && StringUtils.hasText(job.getCron())) {
                taskRegistrar.addCronTask(() -> runJob(job.getCode(), "cron"), job.getCron());
                log.info("定时任务已注册: {} [{}] cron={}", job.getName(), job.getCode(), job.getCron());
            }
        }
    }

    /**
     * 任务列表（含运行状态）
     */
    public List<JobInfo> listJobs() {
        return jobRegistry.list().stream()
                .map(j -> new JobInfo(j.getCode(), j.getName(), j.getCron(),
                        Boolean.TRUE.equals(runningJobs.get(j.getCode()))))
                .toList();
    }

    /**
     * 执行任务（cron 定时或 manual 手动触发），成功/失败均落库
     */
    public void runJob(String code, String triggerType) {
        JobDefinition job = jobRegistry.getByCode(code);
        if (job == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "任务不存在: " + code);
        }
        // 防重入：同一任务未结束前跳过本次触发
        if (runningJobs.putIfAbsent(code, Boolean.TRUE) != null) {
            log.warn("任务 [{}] 正在执行中，跳过本次触发", code);
            return;
        }
        long start = System.currentTimeMillis();
        boolean success = false;
        String errorMsg = null;
        try {
            job.getTask().execute();
            success = true;
        } catch (Exception e) {
            errorMsg = e.getMessage();
            log.error("定时任务 [{}] 执行失败", code, e);
        } finally {
            runningJobs.remove(code);
            saveLog(job, triggerType, success, errorMsg, System.currentTimeMillis() - start);
        }
    }

    /**
     * 执行记录分页查询
     */
    public PageResult<JobLog> pageLogs(String jobCode, Integer result, long page, long size) {
        LambdaQueryWrapper<JobLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(jobCode), JobLog::getJobCode, jobCode)
                .eq(result != null, JobLog::getResult, result)
                .orderByDesc(JobLog::getId);
        return PageResult.of(jobLogMapper.selectPage(new Page<>(page, size), wrapper));
    }

    private void saveLog(JobDefinition job, String triggerType, boolean success,
                         String errorMsg, long durationMs) {
        try {
            JobLog jobLog = new JobLog();
            jobLog.setJobCode(job.getCode());
            jobLog.setJobName(job.getName());
            jobLog.setTriggerType(triggerType);
            jobLog.setResult(success ? 1 : 0);
            jobLog.setErrorMsg(errorMsg != null && errorMsg.length() > 1000
                    ? errorMsg.substring(0, 1000) : errorMsg);
            jobLog.setDurationMs(durationMs);
            jobLogMapper.insert(jobLog);
        } catch (Exception e) {
            log.warn("记录任务执行日志失败: {}", e.getMessage());
        }
    }
}
