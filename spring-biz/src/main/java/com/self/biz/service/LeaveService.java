package com.self.biz.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.self.common.api.req.page.PagingReq;
import com.self.common.api.req.processes.leave.LeaveApproveReq;
import com.self.common.api.req.processes.leave.LeaveSubmitReq;
import com.self.common.api.resp.processes.leave.LeaveTodoTaskResp;
import com.self.common.domain.ResultEntity;
import com.self.common.enums.ProcessInstanceKeyEnum;
import com.self.common.exception.BizException;
import com.self.common.utils.BeanUtils;
import com.self.common.utils.CurUserUtils;
import com.self.dao.api.page.PagingResp;
import com.self.dao.entity.LeaveInfo;
import com.self.dao.entity.User;
import com.self.dao.service.LeaveInfoService;
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

import java.util.List;
import java.util.Map;
import java.util.Objects;
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

        return ResultEntity.ok(processInstance.getId());
    }

    public ResultEntity<PagingResp<LeaveTodoTaskResp>> getTodoList(PagingReq pagingReq){
        Long userId = CurUserUtils.getUserId();

        PagingResp<LeaveTodoTaskResp> pagingResp = new PagingResp<>();
        pagingResp.setCurrentPage(pagingReq.getCurrentPage());
        pagingResp.setPageSize(pagingReq.getPageSize());

        int startIndex = ((pagingReq.getCurrentPage() - 1) * pagingReq.getPageSize());

        TaskQuery taskQuery = taskService.createTaskQuery()
                .taskAssignee(userId.toString())
                .orderByTaskCreateTime()
                .desc();

        long total = taskQuery.count();
        pagingResp.setTotalRecord(total);

        if(startIndex >= total){
            pagingResp.setData(Lists.newArrayListWithCapacity(0));

            return ResultEntity.ok(pagingResp);
        }

        List<Task> taskList = taskQuery.listPage(startIndex, pagingReq.getPageSize());
        List<LeaveTodoTaskResp> respList = taskList.stream().map(task -> BeanUtils.copyProperties(task, LeaveTodoTaskResp.class)).collect(Collectors.toList());

        List<Long> userIds = respList.stream().map(LeaveTodoTaskResp::getAssignee).map(Long::parseLong).collect(Collectors.toList());
        Map<Long, String> userRealNameMap = userDaoService.listByIds(userIds).stream().collect(Collectors.toMap(User::getId, User::getRealName));

        respList.forEach(resp -> resp.setAssigneeRealName(userRealNameMap.getOrDefault(Long.parseLong(resp.getAssignee()), null)));

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
                LambdaQueryWrapper<LeaveInfo> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(LeaveInfo::getProcessInstanceId, task.getProcessInstanceId());

                LeaveInfo leaveInfo = leaveInfoService.getOne(queryWrapper);
                if(Objects.nonNull(leaveInfo)){
                    LeaveInfo editLeaveInfo = new LeaveInfo();
                    editLeaveInfo.setId(leaveInfo.getId());
                    //同意
                    editLeaveInfo.setStatus(1);
                    leaveInfoService.updateById(editLeaveInfo);
                }
            }
        }else{
            //驳回：直接终止流程
            runtimeService.deleteProcessInstance(task.getProcessInstanceId(), "审批驳回");

            LambdaQueryWrapper<LeaveInfo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(LeaveInfo::getProcessInstanceId, task.getProcessInstanceId());

            LeaveInfo leaveInfo = leaveInfoService.getOne(queryWrapper);
            if(Objects.nonNull(leaveInfo)){
                LeaveInfo editLeaveInfo = new LeaveInfo();
                editLeaveInfo.setId(leaveInfo.getId());
                //驳回
                editLeaveInfo.setStatus(2);
                leaveInfoService.updateById(editLeaveInfo);
            }
        }

        return ResultEntity.ok();
    }

}
