package com.aicrm.module.system.service;

import com.aicrm.common.PageResult;
import com.aicrm.module.system.entity.Config;

/**
 * 系统参数服务
 */
public interface ConfigService {

    PageResult<Config> pageConfigs(String keyword, long page, long size);

    Config getByKey(String key);

    Config createConfig(Config config);

    Config updateConfig(Config config);

    void deleteConfig(Long id);
}
