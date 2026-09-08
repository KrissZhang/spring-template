package com.self.biz.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Maps;
import com.self.common.api.req.processes.leave.LeaveApproveReq;
import com.self.common.api.req.processes.leave.LeaveSubmitReq;
import com.self.common.domain.ResultEntity;
import com.self.common.enums.ProcessFormStatusEnum;
import com.self.common.enums.ProcessInstanceKeyEnum;
import com.self.common.exception.BizException;
import com.self.common.utils.CurUserUtils;
import com.self.dao.entity.LeaveInfo;
import com.self.dao.service.LeaveInfoService;
import io.micrometer.core.instrument.util.StringUtils;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class LeaveService {

    private static final Logger logger = LoggerFactory.getLogger(LeaveService.class);

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private LeaveInfoService leaveInfoService;

    @Transactional(rollbackFor = {Exception.class, Error.class})
    public ResultEntity<String> submit(LeaveSubmitReq leaveSubmitReq){
        Long userId = CurUserUtils.getUserId();
        Date now = new Date();

        if(StringUtils.isNotBlank(leaveSubmitReq.getTaskId())){
            //重新提交
            Task task = taskService.createTaskQuery().taskId(leaveSubmitReq.getTaskId()).singleResult();
            if(Objects.isNull(task)){
                throw new BizException("任务不存在或已审批");
            }

            LambdaQueryWrapper<LeaveInfo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(LeaveInfo::getProcessInstanceId, task.getProcessInstanceId());

            LeaveInfo leaveInfo = leaveInfoService.getOne(queryWrapper);
            if(Objects.nonNull(leaveInfo)){
                leaveInfo.setDays(leaveSubmitReq.getDays());
                leaveInfo.setReason(leaveSubmitReq.getReason());
                leaveInfo.setStatus(0);
                leaveInfo.setUpdateBy(userId);
                leaveInfo.setUpdateTime(new Date());
                leaveInfoService.updateById(leaveInfo);
            }

            //更新流程变量
            Map<String, Object> varsMap = runtimeService.getVariables(task.getProcessInstanceId());
            varsMap.put("formStatus", ProcessFormStatusEnum.RESUBMIT.getValue());
            varsMap.put("applicant", userId);
            varsMap.put("applicantTime", now);
            varsMap.put("days", leaveSubmitReq.getDays());

            //默认添加当前环节的评论意见
            taskService.addComment(task.getId(), task.getProcessInstanceId(), "APPLY", "申请填报");

            //重新提交，推进至下一任务节点
            taskService.complete(leaveSubmitReq.getTaskId(), varsMap);

            return ResultEntity.ok(task.getProcessInstanceId());
        }else{
            //新建流程
            LeaveInfo leaveInfo = new LeaveInfo();
            leaveInfo.setApplicant(userId);
            leaveInfo.setDays(leaveSubmitReq.getDays());
            leaveInfo.setReason(leaveSubmitReq.getReason());
            leaveInfo.setStatus(0);
            leaveInfo.setCreateBy(userId);
            leaveInfo.setUpdateBy(userId);
            leaveInfoService.save(leaveInfo);

            //设置流程变量
            Map<String, Object> variables = Maps.newHashMap();
            variables.put("formStatus", ProcessFormStatusEnum.FIRST_SUBMIT.getValue());
            variables.put("applicant", userId);
            variables.put("applicantTime", now);
            variables.put("days", leaveSubmitReq.getDays());

            //启动流程实例
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                    ProcessInstanceKeyEnum.LEAVE.getValue(),
                    leaveInfo.getId().toString(),
                    variables
            );

            //回写流程实例id
            LeaveInfo editLeaveInfo = new LeaveInfo();
            editLeaveInfo.setId(leaveInfo.getId());
            editLeaveInfo.setProcessInstanceId(processInstance.getId());
            leaveInfoService.updateById(editLeaveInfo);

            //自动完成申请填报任务
            Task applyTask = taskService.createTaskQuery().processInstanceId(processInstance.getId())
                    .taskDefinitionKey("applyTask")
                    .singleResult();

            //默认添加当前环节的评论意见
            taskService.addComment(applyTask.getId(), applyTask.getProcessInstanceId(), "APPLY", "申请填报");

            taskService.complete(applyTask.getId());

            return ResultEntity.ok(processInstance.getId());
        }
    }

    @Transactional(rollbackFor = {Exception.class, Error.class})
    public ResultEntity<Void> approve(LeaveApproveReq leaveApproveReq){
        Task task = taskService.createTaskQuery().taskId(leaveApproveReq.getTaskId()).singleResult();
        if(Objects.isNull(task)){
            throw new BizException("任务不存在或已审批");
        }

        Boolean approved = leaveApproveReq.getApproved();

        //添加当前环节的审批意见
        taskService.addComment(leaveApproveReq.getTaskId(), task.getProcessInstanceId(), (approved ? "YES" : "NO"), leaveApproveReq.getComment());

        //流程变量
        Map<String, Object> varsMap = runtimeService.getVariables(task.getProcessInstanceId());

        if(approved){
            //同意
            varsMap.put("approveResult", "approved");

            //修改表单状态
            String formStatus = Optional.ofNullable(varsMap.getOrDefault("formStatus", null)).orElse("").toString();
            if(ProcessFormStatusEnum.REJECTED.getValue().equals(formStatus)){
                varsMap.put("formStatus", ProcessFormStatusEnum.RESUBMIT.getValue());
            }

            //推动流程走向下一节点
            taskService.complete(leaveApproveReq.getTaskId(), varsMap);

            //检查流程是否结束，若结束更新请假状态为审批通过
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId()).singleResult();

            if(Objects.isNull(processInstance)){
                //流程结束
                updateLeaveStatus(task, 1);
            }
        }else{
            //驳回
            varsMap.put("approveResult", "rejected");
            varsMap.put("formStatus", ProcessFormStatusEnum.REJECTED.getValue());

            //推动流程回退
            taskService.complete(leaveApproveReq.getTaskId(), varsMap);

            updateLeaveStatus(task, 2);
        }

        return ResultEntity.ok();
    }

    private void updateLeaveStatus(Task task, Integer targetStatus){
        LambdaQueryWrapper<LeaveInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LeaveInfo::getProcessInstanceId, task.getProcessInstanceId());

        LeaveInfo leaveInfo = leaveInfoService.getOne(queryWrapper);
        if(Objects.nonNull(leaveInfo)){
            LeaveInfo editLeaveInfo = new LeaveInfo();
            editLeaveInfo.setId(leaveInfo.getId());
            editLeaveInfo.setStatus(targetStatus);
            leaveInfoService.updateById(editLeaveInfo);
        }
    }

}
