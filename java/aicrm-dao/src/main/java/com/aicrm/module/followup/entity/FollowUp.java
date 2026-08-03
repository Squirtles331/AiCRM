package com.aicrm.module.followup.entity;

import com.aicrm.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 客户跟进记录（3.2.5）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("follow_up")
@Schema(description = "客户跟进记录（3.2.5）")
public class FollowUp extends BaseEntity {

    /** 租户 ID */
    @Schema(description = "租户 ID")
    private Long tenantId;

    /** 关联线索 ID（可空） */
    @Schema(description = "关联线索 ID（可空）")
    private Long leadId;

    /** 关联客户 ID（可空） */
    @Schema(description = "关联客户 ID（可空）")
    private Long customerId;

    /** 跟进人 ID */
    @Schema(description = "跟进人 ID")
    private Long userId;

    /** 跟进内容 */
    @Schema(description = "跟进内容", example = "客户对报价方案有意向，约下周面谈")
    private String content;

    /** 跟进方式：phone/wechat/visit/other */
    @Schema(description = "跟进方式：phone电话/wechat微信/visit拜访/other其他", example = "wechat")
    private String method;

    /** 下次跟进时间 */
    @Schema(description = "下次跟进时间", example = "2026-08-05 10:00:00")
    private LocalDateTime nextTime;
}
