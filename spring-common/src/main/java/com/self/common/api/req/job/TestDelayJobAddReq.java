package com.self.common.api.req.job;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@ApiModel(description = "新增测试添加延迟执行任务参数")
@Data
public class TestDelayJobAddReq {

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

    @Schema(name = "任务开始时间", description = "任务开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @NotNull(message = "任务开始时间不能为空")
    private Date startTime;

    @Schema(name = "任务参数", description = "任务参数")
    private JSONObject params;

}
