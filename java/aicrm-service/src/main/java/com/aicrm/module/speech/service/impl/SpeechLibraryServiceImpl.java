package com.aicrm.module.speech.service.impl;

import com.aicrm.common.PageResult;
import com.aicrm.common.ResultCode;
import com.aicrm.common.context.TenantContext;
import com.aicrm.common.exception.BusinessException;
import com.aicrm.module.ai.notify.KnowledgeSyncNotifier;
import com.aicrm.module.speech.entity.SpeechLibrary;
import com.aicrm.module.speech.mapper.SpeechLibraryMapper;
import com.aicrm.module.speech.service.SpeechLibraryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 话术库服务实现（3.4.4）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SpeechLibraryServiceImpl implements SpeechLibraryService {

    private final SpeechLibraryMapper speechLibraryMapper;
    private final KnowledgeSyncNotifier knowledgeSyncNotifier;

    @Override
    public PageResult<SpeechLibrary> page(String keyword, String category, Integer status, long page, long size) {
        LambdaQueryWrapper<SpeechLibrary> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(StringUtils.hasText(keyword), w -> w
                        .like(SpeechLibrary::getTitle, keyword)
                        .or()
                        .like(SpeechLibrary::getContent, keyword))
                .eq(StringUtils.hasText(category), SpeechLibrary::getCategory, category)
                .eq(status != null, SpeechLibrary::getStatus, status)
                .orderByDesc(SpeechLibrary::getId);
        return PageResult.of(speechLibraryMapper.selectPage(new Page<>(page, size), wrapper));
    }

    @Override
    public SpeechLibrary detail(Long id) {
        return requireSpeech(id);
    }

    @Override
    public SpeechLibrary create(SpeechLibrary speech) {
        if (!StringUtils.hasText(speech.getTitle())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "话术标题不能为空");
        }
        if (!StringUtils.hasText(speech.getContent())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "话术内容不能为空");
        }
        if (!StringUtils.hasText(speech.getCategory())) {
            speech.setCategory("general");
        }
        if (speech.getStatus() == null) {
            speech.setStatus(1);
        }
        speech.setTenantId(TenantContext.getTenantId());
        speechLibraryMapper.insert(speech);
        knowledgeSyncNotifier.notify("speech", speech.getId(), "create", buildDocContent(speech));
        return speech;
    }

    @Override
    public SpeechLibrary update(SpeechLibrary speech) {
        requireSpeech(speech.getId());
        if (!StringUtils.hasText(speech.getContent())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "话术内容不能为空");
        }
        speechLibraryMapper.updateById(speech);
        SpeechLibrary saved = requireSpeech(speech.getId());
        knowledgeSyncNotifier.notify("speech", saved.getId(), "update", buildDocContent(saved));
        return saved;
    }

    @Override
    public void delete(Long id) {
        requireSpeech(id);
        speechLibraryMapper.deleteById(id);
        knowledgeSyncNotifier.notify("speech", id, "delete", null);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        requireSpeech(id);
        speechLibraryMapper.update(null, new LambdaUpdateWrapper<SpeechLibrary>()
                .eq(SpeechLibrary::getId, id)
                .set(SpeechLibrary::getStatus, status));
        knowledgeSyncNotifier.notify("speech", id, "update", buildDocContent(requireSpeech(id)));
    }

    /** 向量化文本：场景分类 + 标题 + 话术内容 */
    private String buildDocContent(SpeechLibrary speech) {
        return "话术（" + (StringUtils.hasText(speech.getCategory()) ? speech.getCategory() : "general")
                + "）：" + speech.getTitle() + "。" + speech.getContent();
    }

    private SpeechLibrary requireSpeech(Long id) {
        SpeechLibrary speech = speechLibraryMapper.selectById(id);
        if (speech == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "话术不存在");
        }
        return speech;
    }
}
