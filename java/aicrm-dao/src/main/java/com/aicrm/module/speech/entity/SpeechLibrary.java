package com.aicrm.module.speech.entity;

import com.aicrm.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 话术库（3.4.4）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("speech_library")
@Schema(description = "话术库")
public class SpeechLibrary extends BaseEntity {

    /** 租户 ID */
    @Schema(description = "租户 ID")
    private Long tenantId;

    /** 话术标题 */
    @Schema(description = "话术标题", example = "开场白-标准版")
    private String title;

    /** 场景分类：general/quote/selection/objection/follow/opening */
    @Schema(description = "场景分类：general通用/quote报价/selection选型/objection异议/follow跟进/opening开场", example = "general")
    private String category;

    /** 话术内容 */
    @Schema(description = "话术内容")
    private String content;

    /** 状态：1 启用 / 0 停用 */
    @Schema(description = "状态：1 启用 / 0 停用", example = "1")
    private Integer status;
}
