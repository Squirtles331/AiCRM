package com.aicrm.module.speech.service;

import com.aicrm.common.PageResult;
import com.aicrm.module.speech.entity.SpeechLibrary;

/**
 * 话术库服务（3.4.4）：通用话术、场景话术分类维护、一键发送适配
 */
public interface SpeechLibraryService {

    /**
     * 话术分页查询（keyword 匹配标题/内容，可按场景分类/状态过滤）
     */
    PageResult<SpeechLibrary> page(String keyword, String category, Integer status, long page, long size);

    /**
     * 话术详情
     */
    SpeechLibrary detail(Long id);

    /**
     * 创建话术（tenantId 取当前租户上下文）
     */
    SpeechLibrary create(SpeechLibrary speech);

    /**
     * 更新话术
     */
    SpeechLibrary update(SpeechLibrary speech);

    /**
     * 删除话术（逻辑删除）
     */
    void delete(Long id);

    /**
     * 话术启停
     */
    void updateStatus(Long id, Integer status);
}
