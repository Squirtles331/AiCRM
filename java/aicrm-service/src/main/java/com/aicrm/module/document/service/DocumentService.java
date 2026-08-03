package com.aicrm.module.document.service;

import com.aicrm.common.PageResult;
import com.aicrm.module.document.entity.Document;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 资料包中心服务
 */
public interface DocumentService extends IService<Document> {

    /**
     * 分页查询资料
     *
     * @param tenantId 租户 ID
     * @param docType  类型（可空）
     * @param page     页码
     * @param size     每页条数
     * @return 分页结果
     */
    PageResult<Document> pageDocuments(Long tenantId, String docType, long page, long size);

    /**
     * 新增资料（首个版本）
     *
     * @param document 资料（tenantId/title/docType 必填）
     * @return 创建后的资料
     */
    Document createDocument(Document document);

    /**
     * 发布资料（status=1，销售侧才可见可发送）
     *
     * @param id 资料 ID
     * @return 发布后的资料
     */
    Document publish(Long id);
}
