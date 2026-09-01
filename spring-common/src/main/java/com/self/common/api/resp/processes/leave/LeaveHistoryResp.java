package com.self.common.api.resp.processes.leave;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@ApiModel(description = "请假历史轨迹响应参数")
@Data
public class LeaveHistoryResp {

    @Schema(name = "节点id", description = "节点id")
    private String activityId;

    @Schema(name = "节点名称", description = "节点名称")
    private String activityName;

    @Schema(name = "节点类型", description = "节点类型")
    private String activityType;

    @Schema(name = "办理人", description = "办理人")
    private String assignee;

    @Schema(name = "办理人真实名称", description = "办理人真实名称")
    private String assigneeRealName;

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

    @Schema(name = "节点状态标识", description = "节点状态标识(CURRENT-当前节点，FINISHED-已完成，APPROVED-已通过，REJECTED-已驳回，PENDING-未到达)")
    private String activityStatus;

}
