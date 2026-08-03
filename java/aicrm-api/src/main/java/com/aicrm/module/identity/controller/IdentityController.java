package com.aicrm.module.identity.controller;

import com.aicrm.common.Result;
import com.aicrm.common.auth.RequirePermission;
import com.aicrm.common.context.TenantContext;
import com.aicrm.module.identity.entity.Identity;
import com.aicrm.module.identity.entity.IdentityMapping;
import com.aicrm.module.identity.service.IdentityService;
import com.aicrm.module.log.annotation.OperLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 渠道身份归一接口（3.2.2）：身份映射关系维护
 */
@Tag(name = "身份归一")
@RestController
@RequestMapping("/api/identities")
@RequiredArgsConstructor
public class IdentityController {

    private final IdentityService identityService;

    @Operation(summary = "查询实体的身份映射列表",
            description = "权限：identity:list。按实体类型+实体 ID 查询其下绑定的全部身份映射（含匹配置信度）。")
    @RequirePermission(perms = "identity:list")
    @GetMapping
    public Result<List<IdentityMapping>> mappings(@Parameter(description = "实体类型：lead线索/contact联系人/customer客户", required = true) @RequestParam String entityType,
                                                  @Parameter(description = "实体 ID", required = true) @RequestParam Long entityId) {
        return Result.ok(identityService.listMappingsByEntity(entityType, entityId));
    }

    @Operation(summary = "身份详情",
            description = "权限：identity:list。按 ID 查询身份详情（identity_type 取值：mobile手机号/email邮箱/social社媒ID/wecom企微ID/whatsapp/domain企业域名）。")
    @RequirePermission(perms = "identity:list")
    @GetMapping("/{id}")
    public Result<Identity> detail(@Parameter(description = "身份 ID", required = true) @PathVariable Long id) {
        return Result.ok(identityService.getById(id));
    }

    @Operation(summary = "手动绑定身份到线索",
            description = "权限：identity:edit。将一条身份（手机号/邮箱/社媒ID/企微ID/WhatsApp/企业域名）手动绑定到指定线索；tenantId 为空时默认当前租户。")
    @ApiResponse(responseCode = "400", description = "身份类型与值不能为空")
    @ApiResponse(responseCode = "1201", description = "线索不存在")
    @OperLog(module = "身份归一", operation = "绑定身份")
    @RequirePermission(perms = "identity:edit")
    @PostMapping("/bind")
    public Result<Void> bind(@Parameter(description = "绑定请求体", required = true) @RequestBody BindRequest req) {
        Long tenantId = req.tenantId() == null ? TenantContext.getTenantId() : req.tenantId();
        identityService.bindToLead(tenantId, req.identityType, req.identityValue, req.entityId, req.source);
        return Result.ok();
    }

    @Operation(summary = "删除身份映射",
            description = "权限：identity:delete。按映射 ID 删除身份与实体之间的绑定关系。")
    @ApiResponse(responseCode = "404", description = "身份映射不存在")
    @OperLog(module = "身份归一", operation = "删除身份映射")
    @RequirePermission(perms = "identity:delete")
    @DeleteMapping("/mappings/{id}")
    public Result<Void> removeMapping(@Parameter(description = "身份映射 ID", required = true) @PathVariable Long id) {
        identityService.removeMapping(id);
        return Result.ok();
    }

    @Operation(summary = "线索合并（次要线索归并到主线索）",
            description = "权限：identity:merge。将次要线索的身份映射、跟进记录等归并到主线索，并删除次要线索；返回合并后的主线索 ID。")
    @ApiResponse(responseCode = "400", description = "不能合并自身")
    @ApiResponse(responseCode = "1201", description = "线索不存在")
    @OperLog(module = "身份归一", operation = "线索合并")
    @RequirePermission(perms = "identity:merge")
    @PostMapping("/leads/{primaryId}/merge/{secondaryId}")
    public Result<Long> mergeLeads(@Parameter(description = "主线索 ID（保留）", required = true) @PathVariable Long primaryId,
                                   @Parameter(description = "次要线索 ID（被合并）", required = true) @PathVariable Long secondaryId) {
        return Result.ok(identityService.mergeLeads(primaryId, secondaryId));
    }

    /** 手动绑定请求体 */
    @Schema(description = "手动绑定身份到线索请求体")
    public record BindRequest(
            @Schema(description = "租户 ID（为空默认当前租户）") Long tenantId,
            @Schema(description = "身份类型：mobile手机号/email邮箱/social社媒ID/wecom企微ID/whatsapp/domain企业域名") String identityType,
            @Schema(description = "身份值（如手机号、邮箱地址、社媒/企微/WhatsApp ID、企业域名）") String identityValue,
            @Schema(description = "目标线索 ID") Long entityId,
            @Schema(description = "绑定来源标识（如 manual手动）") String source) {
    }
}
