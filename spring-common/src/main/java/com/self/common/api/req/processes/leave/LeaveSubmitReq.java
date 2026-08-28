package com.self.common.api.req.processes.leave;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel(description = "提交请假申请参数")
@Data
public class LeaveSubmitReq {

    @Schema(name = "请假天数", description = "请假天数")
    @NotNull(message = "请假天数不能为空")
    private Integer days;

    @Schema(name = "请假原因", description = "请假原因")
    private String reason;

}
