package com.aicrm.module.file.service;

import com.aicrm.common.PageResult;
import com.aicrm.module.file.entity.FileRecord;
import com.aicrm.module.file.dto.FileUploadVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务
 */
public interface FileService {

    /**
     * 上传文件（落库元数据并返回可访问 URL）
     */
    FileUploadVO upload(MultipartFile file);

    /**
     * 文件元数据
     */
    FileRecord getFile(Long id);

    /**
     * 下载文件内容字节
     */
    byte[] download(Long id);

    /**
     * 删除文件（删除存储对象与元数据，逻辑删除元数据）
     */
    void deleteFile(Long id);

    /**
     * 当前租户文件分页
     */
    PageResult<FileRecord> pageFiles(long page, long size);
}
