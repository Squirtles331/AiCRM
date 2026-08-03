package com.aicrm.module.product.service.impl;

import com.aicrm.common.PageResult;
import com.aicrm.common.ResultCode;
import com.aicrm.common.context.TenantContext;
import com.aicrm.common.exception.BusinessException;
import com.aicrm.module.ai.notify.KnowledgeSyncNotifier;
import com.aicrm.module.product.entity.Product;
import com.aicrm.module.product.mapper.ProductMapper;
import com.aicrm.module.product.service.ProductService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 产品库服务实现（3.4.1）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private final KnowledgeSyncNotifier knowledgeSyncNotifier;

    @Override
    public PageResult<Product> page(String keyword, Long categoryId, Integer status, long page, long size) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(keyword), Product::getName, keyword)
                .eq(categoryId != null, Product::getCategoryId, categoryId)
                .eq(status != null, Product::getStatus, status)
                .orderByDesc(Product::getId);
        return PageResult.of(this.page(new Page<>(page, size), wrapper));
    }

    @Override
    public Product detail(Long id) {
        return requireProduct(id);
    }

    @Override
    public Product create(Product product) {
        if (!StringUtils.hasText(product.getName())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "产品名称不能为空");
        }
        if (product.getStatus() == null) {
            product.setStatus(1);
        }
        product.setTenantId(TenantContext.getTenantId());
        this.save(product);
        log.info("产品创建 id={}, name={}", product.getId(), product.getName());
        knowledgeSyncNotifier.notify("product", product.getId(), "create", buildDocContent(product));
        return product;
    }

    @Override
    public Product update(Product product) {
        requireProduct(product.getId());
        this.updateById(product);
        Product saved = requireProduct(product.getId());
        knowledgeSyncNotifier.notify("product", saved.getId(), "update", buildDocContent(saved));
        return saved;
    }

    @Override
    public void delete(Long id) {
        requireProduct(id);
        this.removeById(id);
        knowledgeSyncNotifier.notify("product", id, "delete", null);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        requireProduct(id);
        this.update(null, new LambdaUpdateWrapper<Product>()
                .eq(Product::getId, id)
                .set(Product::getStatus, status));
        // 上下架同样影响向量检索范围，通知同步
        knowledgeSyncNotifier.notify("product", id, "update", buildDocContent(requireProduct(id)));
    }

    /** 向量化文本：名称 + 规格 + 描述 */
    private String buildDocContent(Product product) {
        StringBuilder sb = new StringBuilder();
        sb.append("产品：").append(product.getName());
        if (StringUtils.hasText(product.getSpec())) {
            sb.append("，规格：").append(product.getSpec());
        }
        if (product.getPrice() != null) {
            sb.append("，参考价：").append(product.getPrice()).append("元");
        }
        if (StringUtils.hasText(product.getDescription())) {
            sb.append("。").append(product.getDescription());
        }
        return sb.toString();
    }

    private Product requireProduct(Long id) {
        Product product = this.getById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "产品不存在");
        }
        return product;
    }
}
