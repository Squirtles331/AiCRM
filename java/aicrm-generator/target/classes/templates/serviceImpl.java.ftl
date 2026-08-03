package ${package.ServiceImpl};

import com.aicrm.common.PageResult;
import com.aicrm.common.ResultCode;
import com.aicrm.common.exception.BusinessException;
import ${package.Entity}.${table.entityName};
import ${package.Mapper}.${table.mapperName};
import ${package.Service}.${table.serviceName};
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * ${table.comment!} 服务实现
 */
@Service
@RequiredArgsConstructor
public class ${table.serviceImplName} implements ${table.serviceName} {

    private final ${table.mapperName} baseMapper;

    @Override
    public PageResult<${table.entityName}> page(long page, long size) {
        LambdaQueryWrapper<${table.entityName}> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(${table.entityName}::getId);
        return PageResult.of(baseMapper.selectPage(new Page<>(page, size), wrapper));
    }

    @Override
    public ${table.entityName} getById(Long id) {
        ${table.entityName} entity = baseMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "${table.comment!}不存在");
        }
        return entity;
    }

    @Override
    public ${table.entityName} create(${table.entityName} entity) {
        baseMapper.insert(entity);
        return entity;
    }

    @Override
    public ${table.entityName} update(${table.entityName} entity) {
        if (entity.getId() == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "id 不能为空");
        }
        getById(entity.getId());
        baseMapper.updateById(entity);
        return baseMapper.selectById(entity.getId());
    }

    @Override
    public void delete(Long id) {
        getById(id);
        baseMapper.deleteById(id);
    }
}
