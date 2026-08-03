package ${package.Service};

import com.aicrm.common.PageResult;
import ${package.Entity}.${table.entityName};

/**
 * ${table.comment!} 服务
 */
public interface ${table.serviceName} {

    /**
     * 分页查询
     */
    PageResult<${table.entityName}> page(long page, long size);

    /**
     * 详情
     */
    ${table.entityName} getById(Long id);

    /**
     * 创建
     */
    ${table.entityName} create(${table.entityName} entity);

    /**
     * 更新
     */
    ${table.entityName} update(${table.entityName} entity);

    /**
     * 删除
     */
    void delete(Long id);
}
