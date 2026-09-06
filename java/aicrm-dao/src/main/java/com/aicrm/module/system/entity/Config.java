package com.aicrm.module.system.entity;

import com.aicrm.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统参数配置（平台级）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_config")
@Schema(description = "系统参数配置")
public class Config extends BaseEntity {

    /** 参数键 */
    @Schema(description = "参数键", example = "system.siteName")
    private String configKey;

    /** 参数值 */
    @Schema(description = "参数值", example = "销售线索系统")
    private String configValue;

    /** 参数名称 */
    @Schema(description = "参数名称", example = "站点名称")
    private String configName;

    /** 类型：1 内置 / 2 自定义 */
    @Schema(description = "类型：1 内置 / 2 自定义", example = "2")
    private Integer configType;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
