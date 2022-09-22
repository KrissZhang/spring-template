package com.self.common.api.req.page;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 分页请求参数
 */
@ApiModel(value = "分页请求参数")
@Data
public class PagingReq {

    @Schema(name = "当前页数", description = "当前页数", required = true)
    @NotNull(message = "当前页数不能为空")
    private Long currentPage;

    @Schema(name = "每页条数", description = "每页条数", required = true)
    @NotNull(message = "每页条数不能为空")
    private Long pageSize;

}
