package com.self.common.api.req.test;

import com.self.common.api.req.page.PagingReq;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@ApiModel(description = "测试信息列表查询参数")
@Data
public class TestListReq extends PagingReq {

    @Schema(name = "名称", description = "名称")
    private String name;

}
