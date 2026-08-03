package com.aicrm.module.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 文件上传结果
 */
@Data
@Schema(description = "文件上传结果")
public class FileUploadVO {

    /** 文件记录 ID */
    @Schema(description = "文件记录 ID")
    private Long id;

    /** 原始文件名 */
    @Schema(description = "原始文件名")
    private String fileName;

    /** 文件大小（字节） */
    @Schema(description = "文件大小（字节）")
    private Long fileSize;

    /** 内容类型 */
    @Schema(description = "内容类型", example = "image/png")
    private String contentType;

    /** 访问地址 */
    @Schema(description = "访问地址", example = "/api/files/1")
    private String url;
}
