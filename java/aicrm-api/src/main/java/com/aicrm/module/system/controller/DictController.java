package com.aicrm.module.system.controller;

import com.aicrm.common.PageResult;
import com.aicrm.common.Result;
import com.aicrm.common.auth.RequirePermission;
import com.aicrm.module.log.annotation.OperLog;
import com.aicrm.module.system.entity.DictData;
import com.aicrm.module.system.entity.DictType;
import com.aicrm.module.system.service.DictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 字典管理接口
 */
@Tag(name = "字典管理")
@RestController
@RequestMapping("/api/dicts")
@RequiredArgsConstructor
public class DictController {

    private final DictService dictService;

    // ---------- 字典类型 ----------

    @Operation(summary = "字典类型分页", description = "需权限 dict:list")
    @RequirePermission(perms = "dict:list")
    @GetMapping("/types")
    public Result<PageResult<DictType>> pageTypes(
            @Parameter(description = "关键字（字典名称/编码模糊匹配）") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码，默认 1") @RequestParam(defaultValue = "1") long page,
            @Parameter(description = "每页条数，默认 20") @RequestParam(defaultValue = "20") long size) {
        return Result.ok(dictService.pageTypes(keyword, page, size));
    }

    @Operation(summary = "启用字典类型列表", description = "前端下拉框直接调用，仅返回启用状态字典类型，无需登录权限")
    @GetMapping("/types/enabled")
    public Result<List<DictType>> listTypes() {
        return Result.ok(dictService.listTypes());
    }

    @Operation(summary = "创建字典类型", description = "需权限 dict:add")
    @ApiResponse(responseCode = "400", description = "字典类型编码已存在/编码与名称不能为空")
    @OperLog(module = "字典管理", operation = "创建字典类型")
    @RequirePermission(perms = "dict:add")
    @PostMapping("/types")
    public Result<DictType> createType(@RequestBody DictType type) {
        return Result.ok(dictService.createType(type));
    }

    @Operation(summary = "更新字典类型", description = "需权限 dict:edit")
    @ApiResponse(responseCode = "400", description = "字典类型不存在")
    @OperLog(module = "字典管理", operation = "更新字典类型")
    @RequirePermission(perms = "dict:edit")
    @PutMapping("/types/{id}")
    public Result<DictType> updateType(
            @Parameter(description = "字典类型 ID", required = true) @PathVariable Long id,
            @RequestBody DictType type) {
        type.setId(id);
        return Result.ok(dictService.updateType(type));
    }

    @Operation(summary = "删除字典类型", description = "需权限 dict:delete")
    @ApiResponse(responseCode = "400", description = "字典类型不存在")
    @OperLog(module = "字典管理", operation = "删除字典类型")
    @RequirePermission(perms = "dict:delete")
    @DeleteMapping("/types/{id}")
    public Result<Void> deleteType(
            @Parameter(description = "字典类型 ID", required = true) @PathVariable Long id) {
        dictService.deleteType(id);
        return Result.ok();
    }

    // ---------- 字典数据 ----------

    @Operation(summary = "字典数据分页", description = "需权限 dict:list")
    @RequirePermission(perms = "dict:list")
    @GetMapping("/data")
    public Result<PageResult<DictData>> pageData(
            @Parameter(description = "字典类型编码") @RequestParam(required = false) String dictType,
            @Parameter(description = "页码，默认 1") @RequestParam(defaultValue = "1") long page,
            @Parameter(description = "每页条数，默认 20") @RequestParam(defaultValue = "20") long size) {
        return Result.ok(dictService.pageData(dictType, page, size));
    }

    @Operation(summary = "按类型查询启用字典数据（前端下拉）", description = "前端下拉框直接调用，仅返回启用状态字典数据，无需登录权限")
    @GetMapping("/data/type/{dictType}")
    public Result<List<DictData>> listDataByType(
            @Parameter(description = "字典类型编码", required = true, example = "lead_status") @PathVariable String dictType) {
        return Result.ok(dictService.listDataByType(dictType));
    }

    @Operation(summary = "创建字典数据", description = "需权限 dict:add")
    @ApiResponse(responseCode = "400", description = "字典类型、标签、值不能为空")
    @OperLog(module = "字典管理", operation = "创建字典数据")
    @RequirePermission(perms = "dict:add")
    @PostMapping("/data")
    public Result<DictData> createData(@RequestBody DictData data) {
        return Result.ok(dictService.createData(data));
    }

    @Operation(summary = "更新字典数据", description = "需权限 dict:edit")
    @ApiResponse(responseCode = "400", description = "字典数据不存在")
    @OperLog(module = "字典管理", operation = "更新字典数据")
    @RequirePermission(perms = "dict:edit")
    @PutMapping("/data/{id}")
    public Result<DictData> updateData(
            @Parameter(description = "字典数据 ID", required = true) @PathVariable Long id,
            @RequestBody DictData data) {
        data.setId(id);
        return Result.ok(dictService.updateData(data));
    }

    @Operation(summary = "删除字典数据", description = "需权限 dict:delete")
    @ApiResponse(responseCode = "400", description = "字典数据不存在")
    @OperLog(module = "字典管理", operation = "删除字典数据")
    @RequirePermission(perms = "dict:delete")
    @DeleteMapping("/data/{id}")
    public Result<Void> deleteData(
            @Parameter(description = "字典数据 ID", required = true) @PathVariable Long id) {
        dictService.deleteData(id);
        return Result.ok();
    }
}
