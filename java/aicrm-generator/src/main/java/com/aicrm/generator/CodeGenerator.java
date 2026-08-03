package com.aicrm.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DateType;

import java.util.HashMap;
import java.util.Map;

/**
 * 轻量代码生成器：连接数据库读取表结构，按项目统一规范生成 实体/Mapper/Service/Controller 基础 CRUD 代码。
 *
 * <p>用法：</p>
 * <ol>
 *   <li>修改下方「配置区」常量（数据源、模块名、权限前缀、接口路径、表名）</li>
 *   <li>直接运行 main 方法（需数据库可达）</li>
 *   <li>生成结果按包结构输出到对应模块 src/main/java：
 *       实体/Mapper → aicrm-dao；Service/Impl → aicrm-service；Controller → aicrm-api</li>
 * </ol>
 *
 * <p>生成规范：实体继承 BaseEntity（自动忽略 id/created_at/updated_at/deleted 公共字段）；
 * 接口统一返回 Result/PageResult；写操作自动带 @OperLog 留痕；权限码 {perms}:list/add/edit/delete。
 * 生成后可在此基础上补充业务校验与查询条件。</p>
 */
public class CodeGenerator {

    // ==================== 配置区：按需修改 ====================

    /** 数据源（与应用 application-dev.yml 保持一致） */
    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/aicrm";
    private static final String JDBC_USERNAME = "aicrm";
    private static final String JDBC_PASSWORD = "aicrm_dev_123";

    /** 目标模块名（包路径 com.aicrm.module.{module}，如 product） */
    private static final String MODULE = "product";
    /** 接口权限前缀（@RequirePermission 权限码，如 product） */
    private static final String PERMS = "product";
    /** 接口路径（/api/{path}，如 products） */
    private static final String API_PATH = "products";
    /** 生成表（可多个） */
    private static final String[] TABLES = {"product"};

    /** 作者 */
    private static final String AUTHOR = "aicrm";

    // ========================================================

    public static void main(String[] args) {
        String daoDir = projectDir() + "/aicrm-dao/src/main/java";
        String serviceDir = projectDir() + "/aicrm-service/src/main/java";
        String apiDir = projectDir() + "/aicrm-api/src/main/java";

        // 各类型输出目录（覆盖默认 outputDir）
        Map<OutputFile, String> pathInfo = new HashMap<>();
        pathInfo.put(OutputFile.entity, daoDir);
        pathInfo.put(OutputFile.mapper, daoDir);
        pathInfo.put(OutputFile.service, serviceDir);
        pathInfo.put(OutputFile.serviceImpl, serviceDir);
        pathInfo.put(OutputFile.controller, apiDir);

        // 模板自定义变量：controller 中 /api/{moduleName} 与权限码 {perms}
        Map<String, Object> customMap = new HashMap<>();
        customMap.put("moduleName", API_PATH);
        customMap.put("perms", PERMS);

        FastAutoGenerator.create(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD)
                .globalConfig(builder -> builder
                        .author(AUTHOR)
                        .dateType(DateType.TIME_PACK)
                        .outputDir(daoDir)
                        .disableOpenDir())
                .packageConfig(builder -> builder
                        .parent("com.aicrm.module")
                        .moduleName(MODULE)
                        .pathInfo(pathInfo))
                .injectionConfig(builder -> builder.customMap(customMap).build())
                .strategyConfig(builder -> builder
                        .addInclude(TABLES)
                        .addTablePrefix("sys_")
                        .entityBuilder()
                        .superClass("com.aicrm.common.base.BaseEntity")
                        .addSuperEntityColumns("id", "created_at", "updated_at", "deleted")
                        .enableLombok()
                        .logicDeleteColumnName("deleted")
                        .disableSerialVersionUID()
                        .mapperBuilder())
                .templateConfig(builder -> builder
                        .disable(com.baomidou.mybatisplus.generator.config.TemplateType.XML)
                        .entity("/templates/entity.java.ftl")
                        .mapper("/templates/mapper.java.ftl")
                        .service("/templates/service.java.ftl")
                        .serviceImpl("/templates/serviceImpl.java.ftl")
                        .controller("/templates/controller.java.ftl"))
                .execute();

        System.out.println("代码生成完成，请检查 aicrm-dao / aicrm-service / aicrm-api 对应包目录。");
    }

    private static String projectDir() {
        // 运行目录为 aicrm-generator，向上 1 级到 java 根目录
        return System.getProperty("user.dir") + "/..";
    }
}
