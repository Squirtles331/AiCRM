package com.aicrm.module.system.entity;

import com.aicrm.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单/权限（平台级定义，租户通过角色-菜单关联获得）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
@Schema(description = "菜单/权限（平台级定义，租户通过角色-菜单关联获得）")
public class Menu extends BaseEntity {

    /** 父菜单 ID，顶级为 0 */
    @Schema(description = "父菜单 ID，顶级为 0", example = "0")
    private Long parentId;

    /** 菜单名称 */
    @Schema(description = "菜单名称")
    private String menuName;

    /** 类型：dir 目录 / menu 菜单 / button 按钮 */
    @Schema(description = "类型：dir目录/menu菜单/button按钮", example = "menu")
    private String menuType;

    /** 前端路由路径 */
    @Schema(description = "前端路由路径")
    private String path;

    /** 前端组件 */
    @Schema(description = "前端组件")
    private String component;

    /** 按钮权限码，如 user:add */
    @Schema(description = "按钮权限码，如 user:add", example = "user:add")
    private String perms;

    /** 图标 */
    @Schema(description = "图标")
    private String icon;

    /** 排序号 */
    @Schema(description = "排序号")
    private Integer sort;

    /** 是否显示：1 显示 / 0 隐藏 */
    @Schema(description = "是否显示：1显示/0隐藏", example = "1")
    private Integer visible;

    /** 状态：1 启用 / 0 停用 */
    @Schema(description = "状态：1启用/0停用", example = "1")
    private Integer status;

    /** 子菜单（树形展示用，非表字段） */
    @TableField(exist = false)
    @Schema(description = "子菜单（树形展示用，非表字段）")
    private List<Menu> children = new ArrayList<>();
}
