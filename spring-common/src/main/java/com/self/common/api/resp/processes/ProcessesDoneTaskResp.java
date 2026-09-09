package com.self.common.api.resp.processes;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@ApiModel(description = "流程已办任务列表响应参数")
@Data
public class ProcessesDoneTaskResp {

    @Schema(name = "任务id", description = "任务id")
    private String taskId;

    @Schema(name = "任务办理人id", description = "任务办理人id")
    private String taskAssignee;

    @Schema(name = "任务办理人真实名称", description = "任务办理人真实名称")
    private String taskAssigneeRealName;

    @Schema(name = "任务节点id", description = "任务节点id")
    private String taskActivityId;

    @Schema(name = "任务节点名称", description = "任务节点名称")
    private String taskActivityName;

    @Schema(name = "节点开始时间", description = "节点开始时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @Schema(name = "节点结束时间", description = "节点结束时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @Schema(name = "节点耗时(毫秒)", description = "节点耗时(毫秒)")
    private Long durationInMillis;

    @Schema(name = "节点评论", description = "节点评论")
    private String comment;

    @Schema(name = "流程实例id", description = "流程实例id")
    private String processInstanceId;

    @Schema(name = "业务主键", description = "业务主键")
    private String businessKey;

    @Schema(name = "流程标识KEY", description = "流程标识KEY")
    private String processInstanceKey;

}
