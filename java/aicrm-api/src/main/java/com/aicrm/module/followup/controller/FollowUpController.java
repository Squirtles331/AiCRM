package com.aicrm.module.followup.controller;

import com.aicrm.common.PageResult;
import com.aicrm.common.Result;
import com.aicrm.common.auth.RequirePermission;
import com.aicrm.module.followup.entity.FollowUp;
import com.aicrm.module.followup.service.FollowUpService;
import com.aicrm.module.log.annotation.OperLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 客户跟进记录接口（3.2.5）
 */
@Tag(name = "跟进记录")
@RestController
@RequestMapping("/api/follow-ups")
@RequiredArgsConstructor
public class FollowUpController {

    private final FollowUpService followUpService;

    @Operation(summary = "新增跟进记录（自动同步线索跟进状态）",
            description = "权限：follow:add。新增一条跟进记录；若关联线索，创建后自动将线索状态同步为跟进中（contacting）。")
    @ApiResponse(responseCode = "400", description = "跟进内容不能为空/线索或客户至少关联一个")
    @OperLog(module = "客户跟进", operation = "新增跟进记录")
    @RequirePermission(perms = "follow:add")
    @PostMapping
    public Result<FollowUp> create(@Parameter(description = "跟进记录信息（method 取值：phone电话/wechat微信/visit拜访/other其他）", required = true) @RequestBody FollowUp followUp) {
        return Result.ok(followUpService.create(followUp));
    }

    @Operation(summary = "跟进记录历史查询",
            description = "权限：follow:list。按线索或客户维度分页查询跟进历史，两个维度均可不填（查全部）。")
    @RequirePermission(perms = "follow:list")
    @GetMapping
    public Result<PageResult<FollowUp>> page(@Parameter(description = "关联线索 ID（按线索维度查跟进记录）") @RequestParam(required = false) Long leadId,
                                             @Parameter(description = "关联客户 ID（按客户维度查跟进记录）") @RequestParam(required = false) Long customerId,
                                             @Parameter(description = "页码，默认 1") @RequestParam(defaultValue = "1") long page,
                                             @Parameter(description = "每页条数，默认 20") @RequestParam(defaultValue = "20") long size) {
        return Result.ok(followUpService.page(leadId, customerId, page, size));
    }
}
