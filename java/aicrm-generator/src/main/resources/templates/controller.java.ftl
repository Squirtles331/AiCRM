package ${package.Controller};

import com.aicrm.common.PageResult;
import com.aicrm.common.Result;
import com.aicrm.common.auth.RequirePermission;
import com.aicrm.module.log.annotation.OperLog;
import ${package.Entity}.${table.entityName};
import ${package.Service}.${table.serviceName};
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * ${table.comment!} 接口
 */
@Tag(name = "${table.comment!}")
@RestController
@RequestMapping("/api/${moduleName}")
@RequiredArgsConstructor
public class ${table.controllerName} {

    private final ${table.serviceName} ${table.entityName?uncap_first}Service;

    @Operation(summary = "${table.comment!}分页")
    @RequirePermission(perms = "${perms}:list")
    @GetMapping
    public Result<PageResult<${table.entityName}>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long size) {
        return Result.ok(${table.entityName?uncap_first}Service.page(page, size));
    }

    @Operation(summary = "${table.comment!}详情")
    @RequirePermission(perms = "${perms}:list")
    @GetMapping("/{id}")
    public Result<${table.entityName}> get(@PathVariable Long id) {
        return Result.ok(${table.entityName?uncap_first}Service.getById(id));
    }

    @Operation(summary = "创建${table.comment!}")
    @OperLog(module = "${table.comment!}", operation = "创建${table.comment!}")
    @RequirePermission(perms = "${perms}:add")
    @PostMapping
    public Result<${table.entityName}> create(@RequestBody ${table.entityName} entity) {
        return Result.ok(${table.entityName?uncap_first}Service.create(entity));
    }

    @Operation(summary = "更新${table.comment!}")
    @OperLog(module = "${table.comment!}", operation = "更新${table.comment!}")
    @RequirePermission(perms = "${perms}:edit")
    @PutMapping("/{id}")
    public Result<${table.entityName}> update(@PathVariable Long id, @RequestBody ${table.entityName} entity) {
        entity.setId(id);
        return Result.ok(${table.entityName?uncap_first}Service.update(entity));
    }

    @Operation(summary = "删除${table.comment!}")
    @OperLog(module = "${table.comment!}", operation = "删除${table.comment!}")
    @RequirePermission(perms = "${perms}:delete")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        ${table.entityName?uncap_first}Service.delete(id);
        return Result.ok();
    }
}
