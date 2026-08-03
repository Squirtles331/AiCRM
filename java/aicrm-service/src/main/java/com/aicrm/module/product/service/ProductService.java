package com.aicrm.module.product.service;

import com.aicrm.common.PageResult;
import com.aicrm.module.product.entity.Product;

/**
 * 产品库服务（3.4.1）：产品分类、参数结构化存储、附件管理、上下架控制
 */
public interface ProductService {

    /**
     * 产品分页查询（管理端全量，可按下架过滤）
     */
    PageResult<Product> page(String keyword, Long categoryId, Integer status, long page, long size);

    /**
     * 产品详情
     */
    Product detail(Long id);

    /**
     * 创建产品（tenantId 取当前租户上下文）
     */
    Product create(Product product);

    /**
     * 更新产品（含参数/附件）
     */
    Product update(Product product);

    /**
     * 删除产品（逻辑删除）
     */
    void delete(Long id);

    /**
     * 上下架控制
     */
    void updateStatus(Long id, Integer status);
}
