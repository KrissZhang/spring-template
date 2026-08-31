package com.self.biz.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.self.common.api.req.page.PagingReq;
import com.self.common.api.req.processes.leave.LeaveApproveReq;
import com.self.common.api.req.processes.leave.LeaveSubmitReq;
import com.self.common.api.resp.processes.leave.LeaveTodoTaskResp;
import com.self.common.constants.CommonConstants;
import com.self.common.domain.ResultEntity;
import com.self.common.enums.ProcessFormStatusEnum;
import com.self.common.enums.ProcessInstanceKeyEnum;
import com.self.common.exception.BizException;
import com.self.common.utils.CurUserUtils;
import com.self.dao.api.page.PagingResp;
import com.self.dao.entity.LeaveInfo;
import com.self.dao.entity.User;
import com.self.dao.service.LeaveInfoService;
import io.micrometer.core.instrument.util.StringUtils;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LeaveService {

    private static final Logger logger = LoggerFactory.getLogger(LeaveService.class);

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private com.self.biz.service.UserService userLogicService;

    @Autowired
    private com.self.dao.service.UserService userDaoService;

    @Autowired
    private LeaveInfoService leaveInfoService;

    @Transactional(rollbackFor = {Exception.class, Error.class})
    public ResultEntity<String> submit(LeaveSubmitReq leaveSubmitReq){
        Long userId = CurUserUtils.getUserId();

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
            varsMap.put("days", leaveSubmitReq.getDays());

            runtimeService.setVariables(task.getProcessInstanceId(), varsMap);

            //重新提交，推进至下一任务节点
            taskService.complete(leaveSubmitReq.getTaskId());

            return ResultEntity.ok(task.getProcessInstanceId());
        }else{
            //新建流程
            //保存请假信息
            LeaveInfo leaveInfo = new LeaveInfo();
            leaveInfo.setApplicant(userId);
            leaveInfo.setDays(leaveSubmitReq.getDays());
            leaveInfo.setReason(leaveSubmitReq.getReason());
            //审批中
            leaveInfo.setStatus(0);
            leaveInfo.setCreateBy(userId);
            leaveInfo.setUpdateBy(userId);
            leaveInfoService.save(leaveInfo);

            //设置流程变量
            //获取审批人 -- TODO
            User managerUser = userLogicService.selectUserByUserName("user2");
            User hrUser = userLogicService.selectUserByUserName("user3");

            Map<String, Object> variables = Maps.newHashMap();
            variables.put("applicant", userId);
            variables.put("days", leaveSubmitReq.getDays());
            variables.put("manager", managerUser == null ? null : managerUser.getId());
            variables.put("hr", hrUser == null ? null : hrUser.getId());

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

            Map<String, Object> submitVars = Maps.newHashMap();
            submitVars.put("formStatus", ProcessFormStatusEnum.FIRST_SUBMIT.getValue());

            taskService.complete(applyTask.getId(), submitVars);

            return ResultEntity.ok(processInstance.getId());
        }
    }

    public ResultEntity<PagingResp<LeaveTodoTaskResp>> getTodoList(PagingReq pagingReq){
        Long userId = CurUserUtils.getUserId();

        PagingResp<LeaveTodoTaskResp> pagingResp = new PagingResp<>();
        pagingResp.setCurrentPage(pagingReq.getCurrentPage());
        pagingResp.setPageSize(pagingReq.getPageSize());

        int startIndex = ((pagingReq.getCurrentPage() - 1) * pagingReq.getPageSize());

        TaskQuery taskQuery = taskService.createTaskQuery()
                .taskAssignee(userId.toString())
                .includeProcessVariables()
                .orderByTaskCreateTime()
                .desc();

        long total = taskQuery.count();
        pagingResp.setTotalRecord(total);

        long totalPage = (total + pagingReq.getPageSize() - 1) / pagingReq.getPageSize();
        pagingResp.setTotalPage((int) totalPage);

        if(startIndex >= total){
            pagingResp.setData(Lists.newArrayListWithCapacity(0));
            return ResultEntity.ok(pagingResp);
        }

        List<Task> taskList = taskQuery.listPage(startIndex, pagingReq.getPageSize());
        List<LeaveTodoTaskResp> respList = taskList.stream().map(task -> {
            LeaveTodoTaskResp resp = new LeaveTodoTaskResp();
            resp.setTaskId(task.getId());
            resp.setTaskName(task.getName());
            resp.setTaskDefinitionKey(task.getTaskDefinitionKey());
            resp.setTaskAssignee(task.getAssignee());
            resp.setProcessInstanceId(task.getProcessInstanceId());

            String processDefinitionId = task.getProcessDefinitionId();
            List<String> splitStr = Arrays.asList(org.apache.commons.lang3.StringUtils.split(processDefinitionId, CommonConstants.STR_COLON));
            resp.setProcessInstanceKey(splitStr.get(0));
            resp.setTaskCreateTime(task.getCreateTime());

            Map<String, Object> varsMap = task.getProcessVariables();
            resp.setFormStatus(Optional.ofNullable(varsMap.getOrDefault("formStatus", null)).orElse("").toString());

            return resp;
        }).collect(Collectors.toList());

        List<Long> userIds = respList.stream().map(LeaveTodoTaskResp::getTaskAssignee).map(Long::parseLong).collect(Collectors.toList());
        Map<Long, String> userRealNameMap = userDaoService.listByIds(userIds).stream().collect(Collectors.toMap(User::getId, User::getRealName));

        respList.forEach(resp -> resp.setTaskAssigneeRealName(userRealNameMap.getOrDefault(Long.parseLong(resp.getTaskAssignee()), null)));

        pagingResp.setData(respList);

        return ResultEntity.ok(pagingResp);
    }

    @Transactional(rollbackFor = {Exception.class, Error.class})
    public ResultEntity<Void> approve(LeaveApproveReq leaveApproveReq){
        Task task = taskService.createTaskQuery().taskId(leaveApproveReq.getTaskId()).singleResult();
        if(Objects.isNull(task)){
            throw new BizException("任务不存在或已审批");
        }

        Boolean approved = leaveApproveReq.getApproved();

        //添加当前环节的审批意见
        taskService.addComment(leaveApproveReq.getTaskId(), task.getProcessInstanceId(), (approved ? "同意" : "驳回"), leaveApproveReq.getComment());

        if(approved){
            //同意：推动流程走向下一节点
            taskService.complete(leaveApproveReq.getTaskId());

            //检查流程是否结束，若结束更新请假状态为审批通过
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId()).singleResult();

            if(Objects.isNull(processInstance)){
                //流程结束
                updateLeaveStatus(task, 1);
            }
        }else{
            //驳回
            rejectTask(task);

            updateLeaveStatus(task, 2);
        }

        return ResultEntity.ok();
    }

    private void rejectTask(Task curTask){
        //当前任务节点KEY
        String curTaskDefinitionKey = curTask.getTaskDefinitionKey();

        //退回目标节点KEY
        String targetTaskDefinitionKey = "";

        //跳转活动状态节点
        if("managerTask".equalsIgnoreCase(curTaskDefinitionKey)){
            targetTaskDefinitionKey = "applyTask";
        }else if("hrTask".equalsIgnoreCase(curTaskDefinitionKey)){
            targetTaskDefinitionKey = "managerTask";
        }else{
            throw new BizException("当前节点标识错误，无法退回");
        }

        runtimeService.setVariable(curTask.getProcessInstanceId(), "formStatus", ProcessFormStatusEnum.REJECTED.getValue());

        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(curTask.getProcessInstanceId())
                .moveActivityIdTo(curTaskDefinitionKey, targetTaskDefinitionKey)
                .changeState();
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
