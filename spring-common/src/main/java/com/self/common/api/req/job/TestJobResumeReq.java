package com.self.common.api.req.job;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@ApiModel(description = "恢复测试定时任务参数")
@Data
public class TestJobResumeReq {

    @Schema(name = "任务名称", description = "任务名称")
    @NotBlank(message = "任务名称不能为空")
    private String jName;

    @Schema(name = "任务组", description = "任务组")
    @NotBlank(message = "任务组不能为空")
    private String jGroup;

}
