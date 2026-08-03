package com.aicrm.module.document.service.impl;

import com.aicrm.common.PageResult;
import com.aicrm.common.ResultCode;
import com.aicrm.common.exception.BusinessException;
import com.aicrm.module.document.entity.Document;
import com.aicrm.module.document.mapper.DocumentMapper;
import com.aicrm.module.document.service.DocumentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 资料包中心服务实现
 */
@Service
public class DocumentServiceImpl extends ServiceImpl<DocumentMapper, Document> implements DocumentService {

    @Override
    public PageResult<Document> pageDocuments(Long tenantId, String docType, long page, long size) {
        LambdaQueryWrapper<Document> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(tenantId != null, Document::getTenantId, tenantId)
                .eq(docType != null && !docType.isBlank(), Document::getDocType, docType)
                .orderByDesc(Document::getId);
        return PageResult.of(this.page(new Page<>(page, size), wrapper));
    }

    @Override
    public Document createDocument(Document document) {
        if (document.getTenantId() == null || document.getTitle() == null || document.getDocType() == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "tenantId/title/docType 不能为空");
        }
        if (document.getVersion() == null) {
            document.setVersion(1);
        }
        if (document.getStatus() == null) {
            document.setStatus(0);
        }
        this.save(document);
        return document;
    }

    @Override
    public Document publish(Long id) {
        Document document = this.getById(id);
        if (document == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "资料不存在");
        }
        document.setStatus(1);
        this.updateById(document);
        return document;
    }
}
