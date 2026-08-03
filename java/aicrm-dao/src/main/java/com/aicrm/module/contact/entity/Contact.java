package com.aicrm.module.contact.entity;

import com.aicrm.common.base.BaseEntity;
import com.aicrm.common.handler.JsonbTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 联系人
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "contact", autoResultMap = true)
public class Contact extends BaseEntity {

    /** 租户 ID */
    private Long tenantId;

    /** 所属客户公司 ID */
    private Long customerId;

    /** 姓名 */
    private String name;

    /** 手机号 */
    private String mobile;

    /** 邮箱 */
    private String email;

    /** 职位 */
    private String position;

    /** 部门 */
    private String department;

    /** 企微 ID */
    private String wecomId;

    /** WhatsApp ID */
    private String whatsappId;

    /** 各渠道身份（JSONB） */
    @TableField(typeHandler = JsonbTypeHandler.class)
    private String socialIds;

    /** 是否决策人 */
    private Boolean isDecisionMaker;

    /** 扩展字段（JSONB） */
    @TableField(typeHandler = JsonbTypeHandler.class)
    private String extra;
}
