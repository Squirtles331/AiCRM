package com.aicrm.module.system.service.impl;

import com.aicrm.common.PageResult;
import com.aicrm.common.ResultCode;
import com.aicrm.common.exception.BusinessException;
import com.aicrm.module.system.entity.DictData;
import com.aicrm.module.system.entity.DictType;
import com.aicrm.module.system.mapper.DictDataMapper;
import com.aicrm.module.system.mapper.DictTypeMapper;
import com.aicrm.module.system.service.DictService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 字典服务实现
 */
@Service
@RequiredArgsConstructor
public class DictServiceImpl implements DictService {

    private final DictTypeMapper dictTypeMapper;
    private final DictDataMapper dictDataMapper;

    @Override
    public PageResult<DictType> pageTypes(String keyword, long page, long size) {
        LambdaQueryWrapper<DictType> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(DictType::getDictName, keyword).or().like(DictType::getDictType, keyword));
        }
        wrapper.orderByAsc(DictType::getId);
        return PageResult.of(dictTypeMapper.selectPage(new Page<>(page, size), wrapper));
    }

    @Override
    public List<DictType> listTypes() {
        return dictTypeMapper.selectList(new LambdaQueryWrapper<DictType>()
                .eq(DictType::getStatus, 1)
                .orderByAsc(DictType::getId));
    }

    @Override
    public DictType createType(DictType type) {
        validateType(type);
        if (dictTypeMapper.selectCount(new LambdaQueryWrapper<DictType>()
                .eq(DictType::getDictType, type.getDictType())) > 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "字典类型编码已存在");
        }
        if (type.getStatus() == null) {
            type.setStatus(1);
        }
        dictTypeMapper.insert(type);
        return type;
    }

    @Override
    public DictType updateType(DictType type) {
        if (type.getId() == null || dictTypeMapper.selectById(type.getId()) == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "字典类型不存在");
        }
        dictTypeMapper.updateById(type);
        return dictTypeMapper.selectById(type.getId());
    }

    @Override
    public void deleteType(Long id) {
        if (dictTypeMapper.selectById(id) == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "字典类型不存在");
        }
        dictTypeMapper.deleteById(id);
    }

    @Override
    public PageResult<DictData> pageData(String dictType, long page, long size) {
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<DictData>()
                .eq(StringUtils.hasText(dictType), DictData::getDictType, dictType)
                .orderByAsc(DictData::getSort);
        return PageResult.of(dictDataMapper.selectPage(new Page<>(page, size), wrapper));
    }

    @Override
    public List<DictData> listDataByType(String dictType) {
        return dictDataMapper.selectList(new LambdaQueryWrapper<DictData>()
                .eq(DictData::getDictType, dictType)
                .eq(DictData::getStatus, 1)
                .orderByAsc(DictData::getSort));
    }

    @Override
    public DictData createData(DictData data) {
        validateData(data);
        if (data.getSort() == null) {
            data.setSort(0);
        }
        if (data.getStatus() == null) {
            data.setStatus(1);
        }
        dictDataMapper.insert(data);
        return data;
    }

    @Override
    public DictData updateData(DictData data) {
        if (data.getId() == null || dictDataMapper.selectById(data.getId()) == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "字典数据不存在");
        }
        dictDataMapper.updateById(data);
        return dictDataMapper.selectById(data.getId());
    }

    @Override
    public void deleteData(Long id) {
        if (dictDataMapper.selectById(id) == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "字典数据不存在");
        }
        dictDataMapper.deleteById(id);
    }

    private void validateType(DictType type) {
        if (!StringUtils.hasText(type.getDictType()) || !StringUtils.hasText(type.getDictName())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "字典类型编码与名称不能为空");
        }
    }

    private void validateData(DictData data) {
        if (!StringUtils.hasText(data.getDictType())
                || !StringUtils.hasText(data.getLabel())
                || !StringUtils.hasText(data.getValue())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "字典类型、标签、值不能为空");
        }
    }
}
