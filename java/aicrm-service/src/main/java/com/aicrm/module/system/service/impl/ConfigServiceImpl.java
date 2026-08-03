package com.aicrm.module.system.service.impl;

import com.aicrm.common.PageResult;
import com.aicrm.common.ResultCode;
import com.aicrm.common.exception.BusinessException;
import com.aicrm.module.system.entity.Config;
import com.aicrm.module.system.mapper.ConfigMapper;
import com.aicrm.module.system.service.ConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 系统参数服务实现
 */
@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {

    private final ConfigMapper configMapper;

    @Override
    public PageResult<Config> pageConfigs(String keyword, long page, long size) {
        LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Config::getConfigKey, keyword).or().like(Config::getConfigName, keyword));
        }
        wrapper.orderByAsc(Config::getId);
        return PageResult.of(configMapper.selectPage(new Page<>(page, size), wrapper));
    }

    @Override
    public Config getByKey(String key) {
        if (!StringUtils.hasText(key)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "configKey 不能为空");
        }
        return configMapper.selectOne(new LambdaQueryWrapper<Config>().eq(Config::getConfigKey, key));
    }

    @Override
    public Config createConfig(Config config) {
        if (!StringUtils.hasText(config.getConfigKey())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "configKey 不能为空");
        }
        if (configMapper.selectCount(new LambdaQueryWrapper<Config>()
                .eq(Config::getConfigKey, config.getConfigKey())) > 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "参数键已存在");
        }
        if (config.getConfigType() == null) {
            config.setConfigType(2); // 自定义
        }
        configMapper.insert(config);
        return config;
    }

    @Override
    public Config updateConfig(Config config) {
        if (config.getId() == null || configMapper.selectById(config.getId()) == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "参数不存在");
        }
        configMapper.updateById(config);
        return configMapper.selectById(config.getId());
    }

    @Override
    public void deleteConfig(Long id) {
        Config config = configMapper.selectById(id);
        if (config == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "参数不存在");
        }
        if (config.getConfigType() != null && config.getConfigType() == 1) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "内置参数不允许删除");
        }
        configMapper.deleteById(id);
    }
}
