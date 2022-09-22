package com.self.dao.api.page;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.self.common.config.LongSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页参数响应
 */
@ApiModel(value = "分页参数响应")
@NoArgsConstructor
@Data
public class PagingResp<T> {

    @Schema(name = "当前页数", description = "当前页数")
    private Long currentPage;

    @Schema(name = "每页条数", description = "每页条数")
    private Long pageSize;

    @Schema(name = "总页数", description = "总页数")
    private Long totalPage;

    @Schema(name = "数据总条数", description = "数据总条数")
    @JsonSerialize(using = LongSerializer.class)
    private Long totalRecord;

    @Schema(name = "内容", description = "内容")
    private List<T> data;

    public PagingResp(Page<T> page){
        this.data = page.getRecords();
        this.currentPage = page.getCurrent();
        this.pageSize = page.getSize();
        this.totalPage = page.getPages();
        this.totalRecord = page.getTotal();
    }

    public PagingResp(Page<T> page, List<T> list){
        this.data = list;
        this.currentPage = page.getCurrent();
        this.pageSize = page.getSize();
        this.totalPage = page.getPages();
        this.totalRecord = page.getTotal();
    }

}
