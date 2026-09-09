package com.self.common.api.req.processes;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@ApiModel(description = "部署流程定义请求参数")
@Data
public class ProcessesDeployReq {

    @Schema(name = "bpmn文件名", description = "bpmn文件名")
    @NotBlank(message = "bpmn文件名不能为空")
    private String bpmnFileName;

    @Schema(name = "部署名称", description = "部署名称")
    @NotBlank(message = "部署名称不能为空")
    private String deployName;

}
