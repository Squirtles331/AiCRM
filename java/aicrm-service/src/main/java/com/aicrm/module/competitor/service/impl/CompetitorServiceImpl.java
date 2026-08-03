package com.aicrm.module.competitor.service.impl;

import com.aicrm.common.PageResult;
import com.aicrm.common.ResultCode;
import com.aicrm.common.context.TenantContext;
import com.aicrm.common.exception.BusinessException;
import com.aicrm.module.ai.notify.KnowledgeSyncNotifier;
import com.aicrm.module.competitor.entity.Competitor;
import com.aicrm.module.competitor.entity.CompetitorProduct;
import com.aicrm.module.competitor.mapper.CompetitorMapper;
import com.aicrm.module.competitor.mapper.CompetitorProductMapper;
import com.aicrm.module.competitor.service.CompetitorService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 竞品库服务实现（3.4.2）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CompetitorServiceImpl implements CompetitorService {

    private final CompetitorMapper competitorMapper;
    private final CompetitorProductMapper productMapper;
    private final KnowledgeSyncNotifier knowledgeSyncNotifier;

    // ---------- 竞品主体 ----------

    @Override
    public PageResult<Competitor> page(String keyword, String category, long page, long size) {
        LambdaQueryWrapper<Competitor> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(keyword), Competitor::getName, keyword)
                .eq(StringUtils.hasText(category), Competitor::getCategory, category)
                .orderByDesc(Competitor::getId);
        return PageResult.of(competitorMapper.selectPage(new Page<>(page, size), wrapper));
    }

    @Override
    public Competitor detail(Long id) {
        return requireCompetitor(id);
    }

    @Override
    public Competitor create(Competitor competitor) {
        if (!StringUtils.hasText(competitor.getName())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "竞品名称不能为空");
        }
        if (competitor.getStatus() == null) {
            competitor.setStatus(1);
        }
        competitor.setTenantId(TenantContext.getTenantId());
        competitorMapper.insert(competitor);
        knowledgeSyncNotifier.notify("competitor", competitor.getId(), "create", buildDocContent(competitor));
        return competitor;
    }

    @Override
    public Competitor update(Competitor competitor) {
        requireCompetitor(competitor.getId());
        competitorMapper.updateById(competitor);
        Competitor saved = requireCompetitor(competitor.getId());
        knowledgeSyncNotifier.notify("competitor", saved.getId(), "update", buildDocContent(saved));
        return saved;
    }

    @Override
    public void delete(Long id) {
        requireCompetitor(id);
        competitorMapper.deleteById(id);
        productMapper.delete(new LambdaQueryWrapper<CompetitorProduct>()
                .eq(CompetitorProduct::getCompetitorId, id));
        knowledgeSyncNotifier.notify("competitor", id, "delete", null);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        requireCompetitor(id);
        competitorMapper.update(null, new LambdaUpdateWrapper<Competitor>()
                .eq(Competitor::getId, id)
                .set(Competitor::getStatus, status));
        knowledgeSyncNotifier.notify("competitor", id, "update", buildDocContent(requireCompetitor(id)));
    }

    /** 向量化文本：竞品名称 + 品类 + 描述 + 优劣势 */
    private String buildDocContent(Competitor competitor) {
        StringBuilder sb = new StringBuilder();
        sb.append("竞品：").append(competitor.getName());
        if (StringUtils.hasText(competitor.getCategory())) {
            sb.append("，品类：").append(competitor.getCategory());
        }
        if (StringUtils.hasText(competitor.getDescription())) {
            sb.append("。").append(competitor.getDescription());
        }
        if (StringUtils.hasText(competitor.getStrengths())) {
            sb.append("。优势：").append(competitor.getStrengths());
        }
        if (StringUtils.hasText(competitor.getWeaknesses())) {
            sb.append("。劣势：").append(competitor.getWeaknesses());
        }
        return sb.toString();
    }

    // ---------- 竞品产品 ----------

    @Override
    public List<CompetitorProduct> listProducts(Long competitorId) {
        requireCompetitor(competitorId);
        return productMapper.selectList(new LambdaQueryWrapper<CompetitorProduct>()
                .eq(CompetitorProduct::getCompetitorId, competitorId)
                .orderByDesc(CompetitorProduct::getId));
    }

    @Override
    public CompetitorProduct createProduct(CompetitorProduct product) {
        requireCompetitor(product.getCompetitorId());
        if (!StringUtils.hasText(product.getProductName())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "竞品产品名称不能为空");
        }
        product.setTenantId(TenantContext.getTenantId());
        productMapper.insert(product);
        return product;
    }

    @Override
    public CompetitorProduct updateProduct(CompetitorProduct product) {
        requireProduct(product.getId());
        productMapper.updateById(product);
        return requireProduct(product.getId());
    }

    @Override
    public void deleteProduct(Long id) {
        requireProduct(id);
        productMapper.deleteById(id);
    }

    // ---------- 基础 ----------

    private Competitor requireCompetitor(Long id) {
        Competitor competitor = competitorMapper.selectById(id);
        if (competitor == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "竞品不存在");
        }
        return competitor;
    }

    private CompetitorProduct requireProduct(Long id) {
        CompetitorProduct product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "竞品产品不存在");
        }
        return product;
    }
}
