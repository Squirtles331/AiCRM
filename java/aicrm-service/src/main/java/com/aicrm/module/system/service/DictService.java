package com.aicrm.module.system.service;

import com.aicrm.common.PageResult;
import com.aicrm.module.system.entity.DictData;
import com.aicrm.module.system.entity.DictType;

import java.util.List;

/**
 * 字典服务（平台级）
 */
public interface DictService {

    /**
     * 字典类型分页
     */
    PageResult<DictType> pageTypes(String keyword, long page, long size);

    /**
     * 全部启用字典类型
     */
    List<DictType> listTypes();

    DictType createType(DictType type);

    DictType updateType(DictType type);

    void deleteType(Long id);

    /**
     * 字典数据分页（按类型过滤）
     */
    PageResult<DictData> pageData(String dictType, long page, long size);

    /**
     * 按类型查询启用字典数据（前端下拉）
     */
    List<DictData> listDataByType(String dictType);

    DictData createData(DictData data);

    DictData updateData(DictData data);

    void deleteData(Long id);
}
