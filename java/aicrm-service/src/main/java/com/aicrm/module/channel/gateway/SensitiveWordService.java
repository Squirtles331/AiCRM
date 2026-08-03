package com.aicrm.module.channel.gateway;

import com.aicrm.module.system.entity.Config;
import com.aicrm.module.system.service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 敏感词服务：读取系统参数 aicrm.sensitive_words（逗号分隔）进行命中检查
 */
@Service
@RequiredArgsConstructor
public class SensitiveWordService {

    /** 敏感词系统参数 key */
    public static final String SENSITIVE_WORDS_KEY = "aicrm.sensitive_words";

    private final ConfigService configService;

    /**
     * 检查内容是否命中敏感词
     *
     * @return 命中的敏感词；未命中返回 null
     */
    public String match(String content) {
        if (!StringUtils.hasText(content)) {
            return null;
        }
        for (String word : words()) {
            if (content.contains(word)) {
                return word;
            }
        }
        return null;
    }

    private List<String> words() {
        Config config = configService.getByKey(SENSITIVE_WORDS_KEY);
        if (config == null || !StringUtils.hasText(config.getConfigValue())) {
            return Collections.emptyList();
        }
        return Arrays.stream(config.getConfigValue().split("[,，]"))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .toList();
    }
}
