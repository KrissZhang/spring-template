package com.self.web.controller.processes;

import com.self.biz.service.LeaveService;
import com.self.common.annotation.OperLog;
import com.self.common.api.req.page.PagingReq;
import com.self.common.api.req.processes.leave.LeaveApproveReq;
import com.self.common.api.req.processes.leave.LeaveSubmitReq;
import com.self.common.api.resp.processes.leave.LeaveTodoTaskResp;
import com.self.common.constants.ApiURI;
import com.self.common.domain.ResultEntity;
import com.self.common.enums.BusinessTypeEnum;
import com.self.dao.api.page.PagingResp;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "请假流程")
@RestController
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    @Operation(summary = "提交请假申请")
    @OperLog(title = "提交请假申请", businessType = BusinessTypeEnum.OTHER)
    @PostMapping(value = ApiURI.PROCESSES_LEAVE_SUBMIT)
    public ResultEntity<String> submit(@RequestBody @Validated LeaveSubmitReq leaveSubmitReq){
        return leaveService.submit(leaveSubmitReq);
    }

    @Operation(summary = "查询请假待办列表")
    @OperLog(title = "查询请假待办列表", businessType = BusinessTypeEnum.OTHER)
    @PostMapping(value = ApiURI.PROCESSES_LEAVE_TODOLIST)
    public ResultEntity<PagingResp<LeaveTodoTaskResp>> getTodoList(@RequestBody @Validated PagingReq pagingReq){
        return leaveService.getTodoList(pagingReq);
    }

    @Operation(summary = "审批请假申请")
    @OperLog(title = "审批请假申请", businessType = BusinessTypeEnum.OTHER)
    @PostMapping(value = ApiURI.PROCESSES_LEAVE_APPROVE)
    public ResultEntity<Void> approve(@RequestBody @Validated LeaveApproveReq leaveApproveReq){
        return leaveService.approve(leaveApproveReq);
    }

}
