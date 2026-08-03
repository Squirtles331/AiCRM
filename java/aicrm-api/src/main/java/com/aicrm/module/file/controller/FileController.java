package com.aicrm.module.file.controller;

import com.aicrm.common.PageResult;
import com.aicrm.common.Result;
import com.aicrm.common.auth.RequirePermission;
import com.aicrm.module.file.dto.FileUploadVO;
import com.aicrm.module.file.entity.FileRecord;
import com.aicrm.module.file.service.FileService;
import com.aicrm.module.log.annotation.OperLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

/**
 * 文件上传下载接口
 */
@Tag(name = "文件管理")
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @Operation(summary = "上传文件", description = "multipart/form-data 表单上传，file 字段为文件流，返回文件记录与访问地址")
    @ApiResponse(responseCode = "400", description = "上传文件不能为空")
    @ApiResponse(responseCode = "500", description = "文件上传失败（业务码 1701）")
    @OperLog(module = "文件管理", operation = "上传文件")
    @PostMapping("/upload")
    public Result<FileUploadVO> upload(
            @Parameter(description = "文件", required = true) @RequestParam("file") MultipartFile file) {
        return Result.ok(fileService.upload(file));
    }

    @Operation(summary = "下载/预览文件", description = "按文件 ID 返回文件内容（响应头带原始文件名与 Content-Type）")
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> download(
            @Parameter(description = "文件记录 ID", required = true) @PathVariable Long id) {
        FileRecord record = fileService.getFile(id);
        byte[] data = fileService.download(id);
        HttpHeaders headers = new HttpHeaders();
        String fileName = record.getFileName() == null ? "file" : record.getFileName();
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename(fileName, StandardCharsets.UTF_8).build());
        MediaType mediaType = record.getContentType() != null
                ? MediaType.parseMediaType(record.getContentType())
                : MediaType.APPLICATION_OCTET_STREAM;
        return ResponseEntity.ok().headers(headers).contentType(mediaType).body(data);
    }

    @Operation(summary = "删除文件", description = "需权限 file:delete")
    @ApiResponse(responseCode = "400", description = "文件不存在")
    @OperLog(module = "文件管理", operation = "删除文件")
    @RequirePermission(perms = "file:delete")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "文件记录 ID", required = true) @PathVariable Long id) {
        fileService.deleteFile(id);
        return Result.ok();
    }

    @Operation(summary = "文件列表分页", description = "需权限 file:list")
    @RequirePermission(perms = "file:list")
    @GetMapping
    public Result<PageResult<FileRecord>> page(
            @Parameter(description = "页码，默认 1") @RequestParam(defaultValue = "1") long page,
            @Parameter(description = "每页条数，默认 20") @RequestParam(defaultValue = "20") long size) {
        return Result.ok(fileService.pageFiles(page, size));
    }
}
