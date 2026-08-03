package com.aicrm.module.product.service;

import com.aicrm.module.product.entity.ProductCategory;

import java.util.List;

/**
 * 产品分类服务（3.4.1）
 */
public interface ProductCategoryService {

    /**
     * 分类列表（树形，按 sort 排序）
     */
    List<ProductCategory> listTree();

    /**
     * 创建分类（name 同租户唯一）
     */
    ProductCategory create(ProductCategory category);

    /**
     * 更新分类
     */
    ProductCategory update(ProductCategory category);

    /**
     * 删除分类（有子分类或产品占用时拒绝）
     */
    void delete(Long id);

    /**
     * 分类启停
     */
    void updateStatus(Long id, Integer status);
}
