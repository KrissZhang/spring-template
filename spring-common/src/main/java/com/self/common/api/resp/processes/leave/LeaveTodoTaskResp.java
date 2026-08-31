package com.self.common.api.resp.processes.leave;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@ApiModel(description = "请假待办任务列表响应参数")
@Data
public class LeaveTodoTaskResp {

    @Schema(name = "任务id", description = "任务id")
    private String taskId;

    @Schema(name = "任务名称", description = "任务名称")
    private String taskName;

    @Schema(name = "任务节点Key", description = "任务节点Key")
    private String taskDefinitionKey;

    @Schema(name = "任务办理人", description = "任务办理人")
    private String taskAssignee;

    @Schema(name = "流程实例id", description = "流程实例id")
    private String processInstanceId;

    @Schema(name = "任务创建时间", description = "任务创建时间")
    private Date taskCreateTime;

    @Schema(name = "任务办理人真实名称", description = "任务办理人真实名称")
    private String taskAssigneeRealName;

}
