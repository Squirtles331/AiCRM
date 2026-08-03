package com.aicrm.module.job;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 任务注册表：收集应用内所有 JobDefinition，提供按编码查询
 */
@Component
public class JobRegistry {

    private final List<JobDefinition> definitions;
    private final Map<String, JobDefinition> byCode = new ConcurrentHashMap<>();

    public JobRegistry(List<JobDefinition> definitions) {
        this.definitions = definitions;
        for (JobDefinition definition : definitions) {
            byCode.put(definition.getCode(), definition);
        }
    }

    public List<JobDefinition> list() {
        return definitions;
    }

    public JobDefinition getByCode(String code) {
        return byCode.get(code);
    }
}
