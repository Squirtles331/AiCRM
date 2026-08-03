package com.aicrm.module.job;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 任务运行状态（前端展示）
 */
@Data
@AllArgsConstructor
public class JobInfo {

    /** 任务编码 */
    private String code;

    /** 任务名称 */
    private String name;

    /** Cron 表达式 */
    private String cron;

    /** 是否在调度中 */
    private boolean running;
}
