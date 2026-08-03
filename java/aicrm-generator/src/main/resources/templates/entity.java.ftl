package ${package.Entity};

import com.aicrm.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ${table.comment!}
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("${table.name}")
@Schema(description = "${table.comment!}")
public class ${table.entityName} extends BaseEntity {

<#list table.fields as field>
    <#if field.comment?? && field.comment?length gt 0>
    /** ${field.comment} */
    @Schema(description = "${field.comment}")
    </#if>
    private ${field.propertyType} ${field.propertyName};
</#list>
}
