package com.self.biz.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.self.common.api.req.page.PagingReq;
import com.self.common.api.req.processes.leave.LeaveApproveReq;
import com.self.common.api.req.processes.leave.LeaveSubmitReq;
import com.self.common.api.resp.processes.leave.LeaveHistoryResp;
import com.self.common.api.resp.processes.leave.LeaveTodoTaskResp;
import com.self.common.constants.CommonConstants;
import com.self.common.domain.ResultEntity;
import com.self.common.enums.ProcessActivityStatusEnum;
import com.self.common.enums.ProcessFormStatusEnum;
import com.self.common.enums.ProcessInstanceKeyEnum;
import com.self.common.exception.BizException;
import com.self.common.utils.CurUserUtils;
import com.self.dao.api.page.PagingResp;
import com.self.dao.entity.LeaveInfo;
import com.self.dao.entity.User;
import com.self.dao.service.LeaveInfoService;
import io.micrometer.core.instrument.util.StringUtils;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.task.Comment;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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
    private HistoryService historyService;

    @Autowired
    private com.self.biz.service.UserService userLogicService;

    @Autowired
    private com.self.dao.service.UserService userDaoService;

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
            variables.put("formStatus", ProcessFormStatusEnum.FIRST_SUBMIT.getValue());
            variables.put("applicant", userId);
            variables.put("applicantTime", now);
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

            //默认添加当前环节的评论意见
            taskService.addComment(applyTask.getId(), applyTask.getProcessInstanceId(), "APPLY", "申请填报");

            taskService.complete(applyTask.getId());

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
            resp.setTaskCreateTime(task.getCreateTime());
            resp.setTaskAssignee(task.getAssignee());
            resp.setTaskActivityId(task.getTaskDefinitionKey());
            resp.setTaskActivityName(task.getName());
            resp.setProcessInstanceId(task.getProcessInstanceId());

            List<String> splitStr = Arrays.asList(org.apache.commons.lang3.StringUtils.split(task.getProcessDefinitionId(), CommonConstants.STR_COLON));
            resp.setProcessInstanceKey(splitStr.get(0));

            //流程变量
            Map<String, Object> varsMap = task.getProcessVariables();

            resp.setProcessApplicant(Optional.ofNullable(varsMap.getOrDefault("applicant", null)).orElse("").toString());

            Object applicantTimeObj = varsMap.getOrDefault("applicantTime", null);
            Date applicantTime = (applicantTimeObj == null ? null : (Date)applicantTimeObj);
            resp.setProcessApplicantTime(applicantTime);

            resp.setFormStatus(Optional.ofNullable(varsMap.getOrDefault("formStatus", null)).orElse("").toString());

            return resp;
        }).collect(Collectors.toList());

        Set<Long> userIds = respList.stream().map(LeaveTodoTaskResp::getTaskAssignee).filter(StringUtils::isNotBlank).map(Long::parseLong).collect(Collectors.toSet());
        Set<Long> applicantUserIds = respList.stream().map(LeaveTodoTaskResp::getProcessApplicant).filter(StringUtils::isNotBlank).map(Long::parseLong).collect(Collectors.toSet());
        userIds.addAll(applicantUserIds);

        Map<Long, String> userRealNameMap = userDaoService.listByIds(userIds).stream().collect(Collectors.toMap(User::getId, User::getRealName));

        respList.forEach(resp -> resp.setTaskAssigneeRealName(userRealNameMap.getOrDefault(Long.parseLong(resp.getTaskAssignee()), null)));

        respList.forEach(resp -> resp.setProcessApplicantRealName(userRealNameMap.getOrDefault(Long.parseLong(resp.getProcessApplicant()), null)));

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
        taskService.addComment(leaveApproveReq.getTaskId(), task.getProcessInstanceId(), (approved ? "YES" : "NO"), leaveApproveReq.getComment());

        if(approved){
            //修改表单状态
            Map<String, Object> varsMap = runtimeService.getVariables(task.getProcessInstanceId());
            String formStatus = Optional.ofNullable(varsMap.getOrDefault("formStatus", null)).orElse("").toString();
            if(ProcessFormStatusEnum.REJECTED.getValue().equals(formStatus)){
                varsMap.put("formStatus", ProcessFormStatusEnum.RESUBMIT.getValue());
            }

            //同意：推动流程走向下一节点
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
            rejectTask(task);

            updateLeaveStatus(task, 2);
        }

        return ResultEntity.ok();
    }

    private void rejectTask(Task curTask){
        //当前任务节点id
        String curTaskActivityId = curTask.getTaskDefinitionKey();

        //退回目标节点id
        String targetTaskActivityId = "";

        //跳转活动状态节点
        if("managerTask".equalsIgnoreCase(curTaskActivityId)){
            targetTaskActivityId = "applyTask";
        }else if("hrTask".equalsIgnoreCase(curTaskActivityId)){
            targetTaskActivityId = "managerTask";
        }else{
            throw new BizException("当前节点标识错误，无法退回");
        }

        //更新流程变量
        runtimeService.setVariable(curTask.getProcessInstanceId(), "formStatus", ProcessFormStatusEnum.REJECTED.getValue());

        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(curTask.getProcessInstanceId())
                .moveActivityIdTo(curTaskActivityId, targetTaskActivityId)
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

    public ResultEntity<List<LeaveHistoryResp>> getHistoryList(String processInstanceId){
        //查询所有历史活动节点
        List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                //只查询用户任务
                .activityType("userTask")
                .orderByHistoricActivityInstanceStartTime()
                .desc()
                .list();

        if(CollectionUtils.isEmpty(activities)){
            return ResultEntity.ok(Lists.newArrayListWithCapacity(0));
        }

        //查询所有节点评论
        List<Comment> commentList = taskService.getProcessInstanceComments(processInstanceId);

        //查询用户真实姓名
        List<Long> userIds = activities.stream().map(HistoricActivityInstance::getAssignee).filter(StringUtils::isNotBlank).map(Long::parseLong).collect(Collectors.toList());
        Map<Long, String> userRealNameMap = userDaoService.listByIds(userIds).stream().collect(Collectors.toMap(User::getId, User::getRealName));

        //标记当前节点
        List<String> curActivityIds = Lists.newArrayList();
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();

        if(Objects.nonNull(processInstance)){
            //流程仍在进行中
            curActivityIds = runtimeService.getActiveActivityIds(processInstanceId);
        }

        List<LeaveHistoryResp> respList = Lists.newArrayList();

        for (HistoricActivityInstance activity : activities) {
            LeaveHistoryResp resp = new LeaveHistoryResp();

            resp.setActivityId(activity.getActivityId());
            resp.setActivityName(activity.getActivityName());
            resp.setActivityType(activity.getActivityType());
            resp.setAssignee(activity.getAssignee());
            resp.setAssigneeRealName(userRealNameMap.getOrDefault(Long.parseLong(resp.getAssignee()), null));
            resp.setStartTime(activity.getStartTime());
            resp.setEndTime(activity.getEndTime());
            resp.setDurationInMillis(activity.getDurationInMillis());

            Comment curComment = null;
            for (Comment comment : commentList) {
                if(comment.getTaskId().equals(activity.getTaskId())){
                    curComment = comment;
                    break;
                }
            }

            resp.setComment(curComment == null ? null : curComment.getFullMessage());

            //节点状态
            if(curActivityIds.contains(resp.getActivityId()) && Objects.isNull(resp.getEndTime())){
                //当前停留节点(进行中/未完成)
                resp.setActivityStatus(ProcessActivityStatusEnum.CURRENT.getValue());
            }else if(Objects.nonNull(resp.getEndTime())){
                //已完成的节点
                if(Objects.isNull(curComment)){
                    resp.setActivityStatus(ProcessActivityStatusEnum.FINISHED.getValue());
                }else if("YES".equals(curComment.getType())){
                    resp.setActivityStatus(ProcessActivityStatusEnum.APPROVED.getValue());
                }else if("NO".equals(curComment.getType())){
                    resp.setActivityStatus(ProcessActivityStatusEnum.REJECTED.getValue());
                }else{
                    resp.setActivityStatus(ProcessActivityStatusEnum.FINISHED.getValue());
                }
            }else{
                resp.setActivityStatus(ProcessActivityStatusEnum.PENDING.getValue());
            }

            respList.add(resp);
        }

        return ResultEntity.ok(respList);
    }

}
