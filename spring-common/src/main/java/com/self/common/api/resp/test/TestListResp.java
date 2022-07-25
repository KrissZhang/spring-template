package com.self.common.api.resp.test;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@ApiModel(description = "测试信息列表响应参数")
@Data
public class TestListResp {

    @Schema(name = "id", description = "id")
    private Integer id;

    @Schema(name = "名称", description = "名称")
    private String name;

    @Schema(name = "值", description = "值")
    private String value;

    @Schema(name = "是否启用", description = "是否启用")
    private Byte enable;

    @Schema(name = "启用状态名称", description = "启用状态名称")
    private String enableName;

}
