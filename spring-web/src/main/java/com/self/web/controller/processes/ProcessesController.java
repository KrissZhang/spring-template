package com.self.web.controller.processes;

import com.self.biz.service.ProcessesService;
import com.self.common.annotation.OperLog;
import com.self.common.api.req.page.PagingReq;
import com.self.common.api.resp.processes.ProcessesDoneTaskResp;
import com.self.common.api.resp.processes.ProcessesHistoryResp;
import com.self.common.api.resp.processes.ProcessesTodoTaskResp;
import com.self.common.constants.ApiURI;
import com.self.common.domain.ResultEntity;
import com.self.common.enums.BusinessTypeEnum;
import com.self.dao.api.page.PagingResp;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import org.flowable.common.engine.impl.util.IoUtil;
import org.flowable.engine.repository.Deployment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Api(tags = "通用流程")
@RestController
public class ProcessesController {

    @Autowired
    private ProcessesService processesService;

    @Operation(summary = "查询流程待办列表")
    @OperLog(title = "查询流程待办列表", businessType = BusinessTypeEnum.OTHER)
    @PostMapping(value = ApiURI.PROCESSES_TODOLIST)
    public ResultEntity<PagingResp<ProcessesTodoTaskResp>> getTodoList(@RequestBody @Validated PagingReq pagingReq){
        return processesService.getTodoList(pagingReq);
    }

    @Operation(summary = "查询流程历史轨迹")
    @OperLog(title = "查询流程历史轨迹", businessType = BusinessTypeEnum.OTHER)
    @GetMapping(value = ApiURI.PROCESSES_HISTORY)
    public ResultEntity<List<ProcessesHistoryResp>> getHistoryList(@RequestParam String processInstanceId){
        return processesService.getHistoryList(processInstanceId);
    }

    @Operation(summary = "查询流程已办列表")
    @OperLog(title = "查询流程已办列表", businessType = BusinessTypeEnum.OTHER)
    @PostMapping(value = ApiURI.PROCESSES_DONELIST)
    public ResultEntity<PagingResp<ProcessesDoneTaskResp>> getDoneList(@RequestBody @Validated PagingReq pagingReq){
        return processesService.getDoneList(pagingReq);
    }

    @Operation(summary = "查询高亮流程图")
    @OperLog(title = "查询高亮流程图", businessType = BusinessTypeEnum.OTHER)
    @GetMapping(value = ApiURI.PROCESSES_DIAGRAM)
    public void getDiagram(HttpServletResponse response, @RequestParam String processInstanceId) throws IOException {
        try(InputStream is = processesService.generateHighLightDiagram(processInstanceId)){
            byte[] bytes = IoUtil.readInputStream(is, "flow-diagram");
            response.setContentType("image/png");
            response.setContentLength(bytes.length);
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
        }
    }

    @Operation(summary = "部署流程定义")
    @OperLog(title = "部署流程定义", businessType = BusinessTypeEnum.OTHER)
    @GetMapping(value = ApiURI.PROCESSES_DEPLOY)
    public ResultEntity<Deployment> deploy(@RequestParam String bpmnFileName, @RequestParam String deployName){
        return processesService.deploy(bpmnFileName, deployName);
    }

}
