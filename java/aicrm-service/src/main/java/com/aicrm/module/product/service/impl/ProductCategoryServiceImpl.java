package com.aicrm.module.product.service.impl;

import com.aicrm.common.ResultCode;
import com.aicrm.common.context.TenantContext;
import com.aicrm.common.exception.BusinessException;
import com.aicrm.module.product.entity.Product;
import com.aicrm.module.product.entity.ProductCategory;
import com.aicrm.module.product.mapper.ProductCategoryMapper;
import com.aicrm.module.product.mapper.ProductMapper;
import com.aicrm.module.product.service.ProductCategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 产品分类服务实现（3.4.1）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final ProductCategoryMapper categoryMapper;
    private final ProductMapper productMapper;

    @Override
    public List<ProductCategory> listTree() {
        List<ProductCategory> all = categoryMapper.selectList(new LambdaQueryWrapper<ProductCategory>()
                .orderByAsc(ProductCategory::getSort));
        List<ProductCategory> roots = new ArrayList<>();
        for (ProductCategory node : all) {
            if (node.getParentId() == null || node.getParentId() == 0) {
                roots.add(node);
            }
        }
        // 展平排序：父在前、子紧随（两级）
        List<ProductCategory> result = new ArrayList<>(roots);
        for (ProductCategory root : roots) {
            for (ProductCategory node : all) {
                if (root.getId().equals(node.getParentId())) {
                    result.add(node);
                }
            }
        }
        return result;
    }

    @Override
    public ProductCategory create(ProductCategory category) {
        if (!StringUtils.hasText(category.getName())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "分类名称不能为空");
        }
        if (category.getParentId() == null) {
            category.setParentId(0L);
        }
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        if (category.getSort() == null) {
            category.setSort(0);
        }
        category.setTenantId(TenantContext.getTenantId());
        categoryMapper.insert(category);
        return category;
    }

    @Override
    public ProductCategory update(ProductCategory category) {
        requireCategory(category.getId());
        categoryMapper.updateById(category);
        return requireCategory(category.getId());
    }

    @Override
    public void delete(Long id) {
        requireCategory(id);
        long childCount = categoryMapper.selectCount(new LambdaQueryWrapper<ProductCategory>()
                .eq(ProductCategory::getParentId, id));
        if (childCount > 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "存在子分类，不能删除");
        }
        long productCount = productMapper.selectCount(new LambdaQueryWrapper<Product>()
                .eq(Product::getCategoryId, id));
        if (productCount > 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "分类下存在产品，不能删除");
        }
        categoryMapper.deleteById(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        requireCategory(id);
        categoryMapper.update(null, new LambdaUpdateWrapper<ProductCategory>()
                .eq(ProductCategory::getId, id)
                .set(ProductCategory::getStatus, status));
    }

    private ProductCategory requireCategory(Long id) {
        ProductCategory category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "产品分类不存在");
        }
        return category;
    }
}
