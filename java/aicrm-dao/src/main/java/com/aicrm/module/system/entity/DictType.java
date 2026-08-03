package com.aicrm.module.system.entity;

import com.aicrm.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典类型（平台级）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict_type")
@Schema(description = "字典类型")
public class DictType extends BaseEntity {

    /** 字典类型编码，如 lead_status */
    @Schema(description = "字典类型编码", example = "lead_status")
    private String dictType;

    /** 字典名称 */
    @Schema(description = "字典名称", example = "线索状态")
    private String dictName;

    /** 状态：1 启用 / 0 停用 */
    @Schema(description = "状态：1 启用 / 0 停用", example = "1")
    private Integer status;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
