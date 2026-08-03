package com.aicrm.module.tenant.controller;

import com.aicrm.common.Result;
import com.aicrm.module.tenant.entity.Plan;
import com.aicrm.module.tenant.service.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 套餐查询接口（创建/更新租户时选择套餐版本）
 */
@Tag(name = "套餐")
@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    @Operation(summary = "套餐列表（仅上架）", description = "登录用户即可访问")
    @GetMapping
    public Result<List<Plan>> list() {
        return Result.ok(planService.listEnabled());
    }

    @Operation(summary = "套餐详情", description = "登录用户即可访问")
    @GetMapping("/{code}")
    public Result<Plan> detail(@Parameter(description = "套餐编码：starter/pro/enterprise", required = true) @PathVariable String code) {
        return Result.ok(planService.getByCode(code));
    }
}
