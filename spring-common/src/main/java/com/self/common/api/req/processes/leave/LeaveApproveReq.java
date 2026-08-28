package com.self.common.api.req.processes.leave;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel(description = "审批请假申请参数")
@Data
public class LeaveApproveReq {

    @Schema(name = "任务id", description = "任务id")
    @NotBlank(message = "任务id不能为空")
    private String taskId;

    @Schema(name = "审批结果", description = "审批结果")
    @NotNull(message = "审批结果不能为空")
    private Boolean approved;

    @Schema(name = "评论", description = "评论")
    private String comment;

}
