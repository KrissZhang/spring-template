package com.self.common.api.req.test;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@ApiModel(description = "新增测试信息参数")
@Data
public class TestAddReq {

    @Schema(name = "名称", description = "名称")
    @NotBlank(message = "名称不能为空")
    private String name;

    @Schema(name = "值", description = "值")
    @NotBlank(message = "值不能为空")
    private String value;

}
