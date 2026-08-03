package com.aicrm.module.job.entity;

import com.aicrm.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 定时任务执行记录（平台级）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_job_log")
@Schema(description = "定时任务执行记录")
public class JobLog extends BaseEntity {

    /** 任务编码 */
    @Schema(description = "任务编码", example = "channelTokenRefresh")
    private String jobCode;

    /** 任务名称 */
    @Schema(description = "任务名称", example = "渠道令牌刷新")
    private String jobName;

    /** 触发方式：cron 定时 / manual 手动 */
    @Schema(description = "触发方式：cron 定时 / manual 手动", example = "cron")
    private String triggerType;

    /** 结果：1 成功 / 0 失败 */
    @Schema(description = "结果：1 成功 / 0 失败", example = "1")
    private Integer result;

    /** 异常信息 */
    @Schema(description = "异常信息")
    private String errorMsg;

    /** 耗时（毫秒） */
    @Schema(description = "耗时（毫秒）", example = "120")
    private Long durationMs;
}
