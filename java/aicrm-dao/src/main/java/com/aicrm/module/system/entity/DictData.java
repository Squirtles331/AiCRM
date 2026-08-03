package com.aicrm.module.system.entity;

import com.aicrm.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典数据（平台级）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict_data")
@Schema(description = "字典数据")
public class DictData extends BaseEntity {

    /** 所属字典类型编码 */
    @Schema(description = "所属字典类型编码", example = "lead_status")
    private String dictType;

    /** 显示文本 */
    @Schema(description = "显示文本", example = "已分配")
    private String label;

    /** 字典值 */
    @Schema(description = "字典值", example = "1")
    private String value;

    /** 排序号 */
    @Schema(description = "排序号（升序排列）", example = "0")
    private Integer sort;

    /** 状态：1 启用 / 0 停用 */
    @Schema(description = "状态：1 启用 / 0 停用", example = "1")
    private Integer status;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
