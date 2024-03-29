package com.self.common.api.req.job;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel(description = "重启测试定时任务参数")
@Data
public class TestJobRescheduleReq {

    @Schema(name = "触发器名称", description = "触发器名称")
    @NotBlank(message = "触发器名称不能为空")
    private String tName;

    @Schema(name = "触发器组", description = "触发器组")
    @NotBlank(message = "触发器组不能为空")
    private String tGroup;

    @Schema(name = "间隔时间(秒)", description = "间隔时间(秒)")
    @NotNull(message = "间隔时间不能为空")
    private Integer intervalTime;

}
