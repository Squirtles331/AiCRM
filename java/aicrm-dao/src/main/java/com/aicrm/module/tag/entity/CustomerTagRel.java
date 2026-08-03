package com.aicrm.module.tag.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 客户-标签关联（3.2.3，唯一约束 tenant+customer+tag）
 * <p>本表仅 created_at，不继承 BaseEntity（无 updated_at/deleted 列）
 */
@Data
@TableName("customer_tag_rel")
public class CustomerTagRel implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 租户 ID */
    private Long tenantId;

    /** 客户 ID */
    private Long customerId;

    /** 标签 ID */
    private Long tagId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
