package com.aicrm.module.file.service.impl;

import com.aicrm.common.PageResult;
import com.aicrm.common.ResultCode;
import com.aicrm.common.exception.BusinessException;
import com.aicrm.module.file.dto.FileUploadVO;
import com.aicrm.module.file.entity.FileRecord;
import com.aicrm.module.file.mapper.FileRecordMapper;
import com.aicrm.module.file.service.FileService;
import com.aicrm.module.file.storage.FileStorage;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl extends ServiceImpl<FileRecordMapper, FileRecord> implements FileService {

    private final FileStorage fileStorage;

    @Override
    public FileUploadVO upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "上传文件不能为空");
        }
        try {
            String storageKey = fileStorage.upload(file.getBytes(), file.getOriginalFilename(), file.getContentType());
            FileRecord record = new FileRecord();
            record.setFileName(file.getOriginalFilename());
            record.setFilePath(storageKey);
            record.setFileSize(file.getSize());
            record.setContentType(file.getContentType());
            record.setStorageType(fileStorage.getClass().getSimpleName().toLowerCase()
                    .contains("minio") ? "minio" : "local");
            this.save(record);

            FileUploadVO vo = new FileUploadVO();
            vo.setId(record.getId());
            vo.setFileName(record.getFileName());
            vo.setFileSize(record.getFileSize());
            vo.setContentType(record.getContentType());
            vo.setUrl("/api/files/" + record.getId());
            return vo;
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException(ResultCode.FILE_UPLOAD_ERROR, "文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public FileRecord getFile(Long id) {
        FileRecord record = getById(id);
        if (record == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "文件不存在");
        }
        return record;
    }

    @Override
    public byte[] download(Long id) {
        FileRecord record = getFile(id);
        try {
            return fileStorage.download(record.getFilePath());
        } catch (IOException e) {
            log.error("文件下载失败 id={}", id, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "文件下载失败");
        }
    }

    @Override
    public void deleteFile(Long id) {
        FileRecord record = getFile(id);
        try {
            fileStorage.delete(record.getFilePath());
        } catch (IOException e) {
            log.warn("删除存储对象失败 id={}, key={}", id, record.getFilePath(), e);
        }
        this.removeById(id);
    }

    @Override
    public PageResult<FileRecord> pageFiles(long page, long size) {
        LambdaQueryWrapper<FileRecord> wrapper = new LambdaQueryWrapper<FileRecord>()
                .orderByDesc(FileRecord::getId);
        return PageResult.of(this.page(new Page<>(page, size), wrapper));
    }
}
