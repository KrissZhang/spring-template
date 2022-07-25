package com.self.common.api.condition.test;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@ApiModel(description = "测试信息列表查询条件")
@Data
public class TestListCondition {

    @Schema(name = "名称", description = "名称")
    private String name;

}
