package com.self.dao.api.page;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.self.common.config.LongSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 分页参数响应
 */
@ApiModel(value = "分页参数响应")
@Data
public class PagingResp<T> {

    @Schema(name = "当前页数", description = "当前页数")
    private Integer currentPage;

    @Schema(name = "每页条数", description = "每页条数")
    private Integer pageSize;

    @Schema(name = "总页数", description = "总页数")
    private Integer totalPage;

    @Schema(name = "数据总条数", description = "数据总条数")
    @JsonSerialize(using = LongSerializer.class)
    private Long totalRecord;

    @Schema(name = "内容", description = "内容")
    private List<T> data;

    public PagingResp() {
    }

    public PagingResp(Page<T> page){
        this.data = page.getRecords();
        this.currentPage = Long.valueOf(page.getCurrent()).intValue();
        this.pageSize = Long.valueOf(page.getSize()).intValue();
        this.totalPage = Long.valueOf(page.getPages()).intValue();
        this.totalRecord = page.getTotal();
    }

    public PagingResp(Page<T> page, List<T> list){
        this.data = list;
        this.currentPage = Long.valueOf(page.getCurrent()).intValue();
        this.pageSize = Long.valueOf(page.getSize()).intValue();
        this.totalPage = Long.valueOf(page.getPages()).intValue();
        this.totalRecord = page.getTotal();
    }

}
