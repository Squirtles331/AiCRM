package com.aicrm.module.competitor.service;

import com.aicrm.common.PageResult;
import com.aicrm.module.competitor.entity.Competitor;
import com.aicrm.module.competitor.entity.CompetitorProduct;

import java.util.List;

/**
 * 竞品库服务（3.4.2）：竞品主体档案、竞品产品参数、优劣势分析、攻防话术维护
 */
public interface CompetitorService {

    // ---------- 竞品主体 ----------

    PageResult<Competitor> page(String keyword, String category, long page, long size);

    Competitor detail(Long id);

    Competitor create(Competitor competitor);

    Competitor update(Competitor competitor);

    void delete(Long id);

    void updateStatus(Long id, Integer status);

    // ---------- 竞品产品 ----------

    List<CompetitorProduct> listProducts(Long competitorId);

    CompetitorProduct createProduct(CompetitorProduct product);

    CompetitorProduct updateProduct(CompetitorProduct product);

    void deleteProduct(Long id);
}
