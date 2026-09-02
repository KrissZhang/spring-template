package com.self.common.api.resp.processes.leave;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@ApiModel(description = "请假待办任务列表响应参数")
@Data
public class LeaveTodoTaskResp {

    @Schema(name = "任务id", description = "任务id")
    private String taskId;

    @Schema(name = "任务创建时间", description = "任务创建时间")
    private Date taskCreateTime;

    @Schema(name = "任务办理人id", description = "任务办理人id")
    private String taskAssignee;

    @Schema(name = "任务办理人真实名称", description = "任务办理人真实名称")
    private String taskAssigneeRealName;

    @Schema(name = "任务节点id", description = "任务节点id")
    private String taskActivityId;

    @Schema(name = "任务节点名称", description = "任务节点名称")
    private String taskActivityName;

    @Schema(name = "流程实例id", description = "流程实例id")
    private String processInstanceId;

    @Schema(name = "流程标识KEY", description = "流程标识KEY")
    private String processInstanceKey;

    @Schema(name = "流程申请人id", description = "流程申请人id")
    private String processApplicant;

    @Schema(name = "流程申请人真实名称", description = "流程申请人真实名称")
    private String processApplicantRealName;

    @Schema(name = "流程申请时间", description = "流程申请时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date processApplicantTime;

    @Schema(name = "表单状态标识", description = "表单状态标识")
    private String formStatus;

}
