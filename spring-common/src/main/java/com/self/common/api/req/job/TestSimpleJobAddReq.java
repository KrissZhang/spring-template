package com.self.common.api.req.job;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel(description = "新增时间间隔测试定时任务参数")
@Data
public class TestSimpleJobAddReq {

    @Schema(name = "任务名称", description = "任务名称")
    @NotBlank(message = "任务名称不能为空")
    private String jName;

    @Schema(name = "任务组", description = "任务组")
    @NotBlank(message = "任务组不能为空")
    private String jGroup;

    @Schema(name = "触发器名称", description = "触发器名称")
    @NotBlank(message = "触发器名称不能为空")
    private String tName;

    @Schema(name = "触发器组", description = "触发器组")
    @NotBlank(message = "触发器组不能为空")
    private String tGroup;

    @Schema(name = "间隔时间(秒)", description = "间隔时间(秒)")
    @NotNull(message = "间隔时间不能为空")
    private Integer intervalTime;

    @Schema(name = "任务参数", description = "任务参数")
    private JSONObject params;

}
