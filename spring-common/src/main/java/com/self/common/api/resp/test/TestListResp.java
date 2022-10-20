package com.self.common.api.resp.test;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@ApiModel(description = "测试信息列表响应参数")
@Data
public class TestListResp {

    @Schema(name = "id", description = "id")
    private Long id;

    @Schema(name = "名称", description = "名称")
    private String name;

    @Schema(name = "值", description = "值")
    private String value;

    @Schema(name = "是否逻辑删除", description = "是否逻辑删除")
    private Byte isDeleted;

    @Schema(name = "是否逻辑删除名称", description = "是否逻辑删除名称")
    private String isDeletedName;

}
