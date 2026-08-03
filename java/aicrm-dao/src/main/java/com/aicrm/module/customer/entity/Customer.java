package com.aicrm.module.customer.entity;

import com.aicrm.common.base.BaseEntity;
import com.aicrm.common.handler.JsonbTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户公司（对应"获取客户公司人员架构"）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "customer", autoResultMap = true)
@Schema(description = "客户公司（对应获取客户公司人员架构）")
public class Customer extends BaseEntity {

    /** 租户 ID */
    @Schema(description = "租户 ID")
    private Long tenantId;

    /** 公司名称 */
    @Schema(description = "公司名称", example = "某某科技有限公司")
    private String name;

    /** 行业 */
    @Schema(description = "行业", example = "软件服务")
    private String industry;

    /** 规模 */
    @Schema(description = "规模", example = "100-499人")
    private String scale;

    /** 地区 */
    @Schema(description = "地区", example = "上海")
    private String region;

    /** 人员架构（授权/公开数据，JSONB） */
    @TableField(typeHandler = JsonbTypeHandler.class)
    @Schema(description = "人员架构（授权/公开数据，JSONB），存储结构示例：{\"executives\":[{\"name\":\"姓名\",\"title\":\"职位\",\"phone\":\"手机号\",\"email\":\"邮箱\"}],\"departments\":[\"部门名\"]}，实际字段以第三方返回为准")
    private String orgStructure;

    /** enrichment 来源：公开数据/客户提供 */
    @Schema(description = "enrichment 来源：public_data公开数据/customer_provided客户提供", example = "public_data")
    private String source;

    /** 数据补全状态：0 未补全 / 1 已补全 */
    @Schema(description = "数据补全状态：0 未补全 / 1 已补全", example = "0")
    private Integer enrichmentStatus;

    /** 客户阶段：new/potential/intention/negotiating/won/lost */
    @Schema(description = "客户阶段：new潜在/potential有意向/intention报价/negotiating谈判/won成交/lost流失", example = "potential")
    private String stage;

    /** 意向等级：0-5 */
    @Schema(description = "意向等级：0-5", example = "3")
    private Integer intentLevel;

    /** 综合评分：0-100 */
    @Schema(description = "综合评分：0-100", example = "80")
    private Integer score;
}
