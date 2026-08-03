package com.aicrm.module.file.entity;

import com.aicrm.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件记录（租户级）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_file")
@Schema(description = "文件记录")
public class FileRecord extends BaseEntity {

    /** 租户 ID */
    @Schema(description = "租户 ID")
    private Long tenantId;

    /** 原始文件名 */
    @Schema(description = "原始文件名", example = "产品手册.pdf")
    private String fileName;

    /** 存储 key（本地为相对路径，MinIO 为对象名） */
    @Schema(description = "存储 key（本地为相对路径，MinIO 为对象名）")
    private String filePath;

    /** 文件大小（字节） */
    @Schema(description = "文件大小（字节）")
    private Long fileSize;

    /** 内容类型 */
    @Schema(description = "内容类型", example = "application/pdf")
    private String contentType;

    /** 存储类型：local / minio */
    @Schema(description = "存储类型：local 本地存储 / minio 对象存储", example = "local")
    private String storageType;
}
