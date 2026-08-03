package com.aicrm.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页返回结构
 */
@Data
@Schema(description = "分页返回结构")
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 当前页（从 1 开始） */
    @Schema(description = "当前页（从 1 开始）", example = "1")
    private long page;

    /** 每页条数 */
    @Schema(description = "每页条数", example = "20")
    private long size;

    /** 总条数 */
    @Schema(description = "总条数", example = "100")
    private long total;

    /** 总页数 */
    @Schema(description = "总页数", example = "5")
    private long pages;

    /** 数据列表 */
    @Schema(description = "数据列表")
    private List<T> records;

    public static <T> PageResult<T> of(IPage<T> page) {
        PageResult<T> r = new PageResult<>();
        r.setPage(page.getCurrent());
        r.setSize(page.getSize());
        r.setTotal(page.getTotal());
        r.setPages(page.getPages());
        r.setRecords(page.getRecords());
        return r;
    }
}
