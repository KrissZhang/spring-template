package com.self.common.api.req.processes.leave;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@ApiModel(description = "撤销请假申请参数")
@Data
public class LeaveRevokeReq {

    @Schema(name = "流程实例id", description = "流程实例id")
    @NotBlank(message = "流程实例id不能为空")
    private String processInstanceId;

}
