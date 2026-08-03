package com.aicrm.module.job;

import lombok.Data;

/**
 * 定时任务定义（代码配置式注册，MVP 暂不做可视化页面）
 */
@Data
public class JobDefinition {

    /** 任务编码（唯一） */
    private String code;

    /** 任务名称 */
    private String name;

    /** Cron 表达式 */
    private String cron;

    /** 是否随应用启动自动调度 */
    private boolean enabled = true;

    /** 任务执行逻辑 */
    private JobTask task;

    @FunctionalInterface
    public interface JobTask {
        void execute() throws Exception;
    }
}
