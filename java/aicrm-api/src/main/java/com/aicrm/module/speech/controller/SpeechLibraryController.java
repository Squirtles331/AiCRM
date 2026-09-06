package com.aicrm.module.speech.controller;

import com.aicrm.common.PageResult;
import com.aicrm.common.Result;
import com.aicrm.common.auth.RequirePermission;
import com.aicrm.module.log.annotation.OperLog;
import com.aicrm.module.speech.entity.SpeechLibrary;
import com.aicrm.module.speech.service.SpeechLibraryService;
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

/**
 * 话术库接口（3.4.4）：通用话术、场景话术分类维护、一键发送适配
 */
@Tag(name = "话术库")
@RestController
@RequestMapping("/api/speech-libraries")
@RequiredArgsConstructor
public class SpeechLibraryController {

    private final SpeechLibraryService speechLibraryService;

    @Operation(summary = "话术分页查询（keyword/场景分类/状态过滤）", description = "话术列表检索，支持关键字/场景分类/状态过滤，场景：话术库管理、一键发送适配；权限：speech:list")
    @RequirePermission(perms = "speech:list")
    @GetMapping
    public Result<PageResult<SpeechLibrary>> page(@Parameter(description = "关键字（话术标题/内容模糊匹配）", required = false) @RequestParam(required = false) String keyword,
                                                  @Parameter(description = "场景分类：general通用/quote报价/selection选型/objection异议/follow跟进/opening开场", required = false) @RequestParam(required = false) String category,
                                                  @Parameter(description = "状态：1 启用 / 0 停用", required = false) @RequestParam(required = false) Integer status,
                                                  @Parameter(description = "页码，默认 1", required = false) @RequestParam(defaultValue = "1") long page,
                                                  @Parameter(description = "每页条数，默认 20", required = false) @RequestParam(defaultValue = "20") long size) {
        return Result.ok(speechLibraryService.page(keyword, category, status, page, size));
    }

    @Operation(summary = "话术详情", description = "按 ID 查询话术内容，场景：话术库管理、一键发送预览；权限：speech:list")
    @RequirePermission(perms = "speech:list")
    @GetMapping("/{id}")
    public Result<SpeechLibrary> detail(@Parameter(description = "话术 ID", required = true) @PathVariable Long id) {
        return Result.ok(speechLibraryService.detail(id));
    }

    @Operation(summary = "创建话术", description = "新增话术（通用/场景分类），场景：话术库管理维护；权限：speech:add。")
    @ApiResponse(responseCode = "400", description = "话术标题/内容不能为空")
    @OperLog(module = "话术库", operation = "创建话术")
    @RequirePermission(perms = "speech:add")
    @PostMapping
    public Result<SpeechLibrary> create(@RequestBody SpeechLibrary speech) {
        return Result.ok(speechLibraryService.create(speech));
    }

    @Operation(summary = "更新话术", description = "修改话术标题/分类/内容，场景：话术库管理维护；权限：speech:edit。")
    @ApiResponse(responseCode = "400", description = "话术内容不能为空")
    @ApiResponse(responseCode = "404", description = "话术不存在")
    @OperLog(module = "话术库", operation = "更新话术")
    @RequirePermission(perms = "speech:edit")
    @PutMapping("/{id}")
    public Result<SpeechLibrary> update(@Parameter(description = "话术 ID", required = true) @PathVariable Long id, @RequestBody SpeechLibrary speech) {
        speech.setId(id);
        return Result.ok(speechLibraryService.update(speech));
    }

    @Operation(summary = "删除话术", description = "删除话术，场景：话术库管理维护；权限：speech:delete。")
    @ApiResponse(responseCode = "404", description = "话术不存在")
    @OperLog(module = "话术库", operation = "删除话术")
    @RequirePermission(perms = "speech:delete")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "话术 ID", required = true) @PathVariable Long id) {
        speechLibraryService.delete(id);
        return Result.ok();
    }

    @Operation(summary = "话术启停", description = "启用/停用话术，停用后不可被一键发送检索，场景：话术库上下架管理；权限：speech:edit。")
    @ApiResponse(responseCode = "404", description = "话术不存在")
    @OperLog(module = "话术库", operation = "话术启停")
    @RequirePermission(perms = "speech:edit")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@Parameter(description = "话术 ID", required = true) @PathVariable Long id, @Parameter(description = "状态：1 启用 / 0 停用", required = true) @RequestParam Integer status) {
        speechLibraryService.updateStatus(id, status);
        return Result.ok();
    }
}
