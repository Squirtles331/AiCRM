package com.aicrm.common.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 实体基类：主键 / 审计字段 / 逻辑删除
 * <p>
 * 所有业务表继承本类，统一约定：id 自增、created_at/updated_at 自动填充、deleted 逻辑删除。
 */
@Data
@Schema(description = "实体基类（主键/审计字段/逻辑删除）")
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键 ID", example = "1")
    private Long id;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间", example = "2026-08-03 12:00:00")
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间", example = "2026-08-03 12:00:00")
    private LocalDateTime updatedAt;

    @TableLogic
    @TableField("deleted")
    @Schema(description = "逻辑删除：0正常/1已删", example = "0", hidden = true)
    private Integer deleted;
}
