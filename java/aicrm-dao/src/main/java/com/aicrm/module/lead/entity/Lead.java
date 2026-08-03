package com.aicrm.module.lead.entity;

import com.aicrm.common.base.BaseEntity;
import com.aicrm.common.handler.JsonbTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 线索（业务主节点）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "lead", autoResultMap = true)
@Schema(description = "线索（业务主节点）")
public class Lead extends BaseEntity {

    /** 租户 ID */
    @Schema(description = "租户 ID")
    private Long tenantId;

    /** 关联客户公司 ID（可空） */
    @Schema(description = "关联客户公司 ID（可空）")
    private Long customerId;

    /** 关联联系人 ID（可空） */
    @Schema(description = "关联联系人 ID（可空）")
    private Long contactId;

    /** 来源渠道账号 ID */
    @Schema(description = "来源渠道账号 ID")
    private Long sourceChannelId;

    /** 来源内容/视频/广告 ID */
    @Schema(description = "来源内容/视频/广告 ID")
    private String sourceContentId;

    /** 来源类型：comment/dm/form/click */
    @Schema(description = "来源类型：comment评论/dm私信/form表单/click点击", example = "comment")
    private String sourceType;

    /** 意向：quote/sample/selection/other */
    @Schema(description = "意向：quote报价/sample样品/selection选型/other其他", example = "quote")
    private String intent;

    /** 状态：new/assigned/contacting/effective/quoted/opportunity/lost */
    @Schema(description = "状态：new新线索/assigned已分配/contacting跟进中/effective有效/quoted已报价/opportunity商机/lost流失", example = "new")
    private String status;

    /** 线索评分 0-100 */
    @Schema(description = "线索评分：0-100", example = "60")
    private Integer score;

    /** 归属坐席 ID */
    @Schema(description = "归属坐席 ID")
    private Long ownerId;

    /** 响应 SLA 截止时间 */
    @Schema(description = "响应 SLA 截止时间", example = "2026-08-03 12:00:00")
    private LocalDateTime slaDeadline;

    /** 抽取关键字段（场景/数量/预算/交期，JSONB） */
    @TableField(typeHandler = JsonbTypeHandler.class)
    @Schema(description = "抽取关键字段（JSONB），存储结构示例：{\"scenario\":\"场景\",\"quantity\":\"数量\",\"budget\":\"预算\",\"delivery\":\"交期\"}")
    private String extra;
}
