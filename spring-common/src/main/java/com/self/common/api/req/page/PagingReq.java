package com.self.common.api.req.page;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 分页请求参数
 */
@ApiModel(value = "分页请求参数")
@Data
public class PagingReq {

    @Schema(name = "当前页数", description = "当前页数", defaultValue = "1", required = true)
    @NotNull(message = "当前页数不能为空")
    @Min(value = 1, message = "当前页数不能小于1")
    private Integer currentPage = 1;

    @Schema(name = "每页条数", description = "每页条数", defaultValue = "10", required = true)
    @NotNull(message = "每页条数不能为空")
    @Min(value = 1, message = "每页条数不能小于1")
    private Integer pageSize = 10;

}
