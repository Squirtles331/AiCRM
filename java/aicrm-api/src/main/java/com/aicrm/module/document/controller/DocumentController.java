package com.aicrm.module.document.controller;

import com.aicrm.common.PageResult;
import com.aicrm.common.Result;
import com.aicrm.common.ResultCode;
import com.aicrm.common.exception.BusinessException;
import com.aicrm.module.document.entity.Document;
import com.aicrm.module.document.service.DocumentService;
import com.aicrm.module.log.annotation.OperLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * 资料包中心接口
 */
@Tag(name = "资料包中心")
@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    /** 本地上传目录（M1 简化；V2 切对象存储） */
    private static final String UPLOAD_DIR = "uploads";

    private final DocumentService documentService;

    @Operation(summary = "分页查询资料", description = "按租户/资料类型过滤")
    @GetMapping
    public Result<PageResult<Document>> page(
            @Parameter(description = "租户 ID（平台管理员可指定，为空默认当前租户）") @RequestParam(required = false) Long tenantId,
            @Parameter(description = "资料类型：product_brochure产品手册/case案例/whitepaper白皮书/selection_table选型表") @RequestParam(required = false) String docType,
            @Parameter(description = "页码，默认 1") @RequestParam(defaultValue = "1") long page,
            @Parameter(description = "每页条数，默认 20") @RequestParam(defaultValue = "20") long size) {
        return Result.ok(documentService.pageDocuments(tenantId, docType, page, size));
    }

    @Operation(summary = "新增资料", description = "请求体为资料信息（不含文件，文件通过上传接口获取 URL 后写入 fileUrl）")
    @ApiResponse(responseCode = "400", description = "tenantId/title/docType 不能为空")
    @OperLog(module = "知识库", operation = "新增资料")
    @PostMapping
    public Result<Document> create(@Valid @RequestBody Document document) {
        return Result.ok(documentService.createDocument(document));
    }

    @Operation(summary = "发布资料", description = "将草稿资料置为已发布状态")
    @ApiResponse(responseCode = "404", description = "资料不存在")
    @OperLog(module = "知识库", operation = "发布资料")
    @PutMapping("/{id}/publish")
    public Result<Document> publish(
            @Parameter(description = "资料 ID", required = true) @PathVariable Long id) {
        return Result.ok(documentService.publish(id));
    }

    @Operation(summary = "上传资料文件（返回可访问 URL）", description = "multipart/form-data 表单上传，返回 /uploads/xxx 形式的访问 URL")
    @ApiResponse(responseCode = "400", description = "文件不能为空")
    @ApiResponse(responseCode = "500", description = "文件上传失败（业务码 1701）")
    @OperLog(module = "知识库", operation = "上传资料文件")
    @PostMapping("/upload")
    public Result<String> upload(
            @Parameter(description = "文件", required = true) @RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "文件不能为空");
        }
        try {
            String original = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename();
            String filename = UUID.randomUUID() + "-" + original.replaceAll("[\\\\/]", "_");
            Path dir = Paths.get(UPLOAD_DIR);
            Files.createDirectories(dir);
            Path target = dir.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return Result.ok("/uploads/" + filename);
        } catch (IOException e) {
            throw new BusinessException(ResultCode.FILE_UPLOAD_ERROR, e.getMessage());
        }
    }
}
