package com.self.biz.service;

import com.google.common.collect.Lists;
import com.self.common.api.req.page.PagingReq;
import com.self.common.api.resp.processes.ProcessesDoneTaskResp;
import com.self.common.api.resp.processes.ProcessesHistoryResp;
import com.self.common.api.resp.processes.ProcessesTodoTaskResp;
import com.self.common.constants.CommonConstants;
import com.self.common.domain.ResultEntity;
import com.self.common.enums.ProcessActivityStatusEnum;
import com.self.common.utils.CurUserUtils;
import com.self.dao.api.page.PagingResp;
import com.self.dao.entity.ActHiComment;
import com.self.dao.entity.User;
import com.self.dao.mapper.ActHiCommentMapper;
import io.micrometer.core.instrument.util.StringUtils;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.task.Comment;
import org.flowable.image.impl.DefaultProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProcessesService {

    private static final Logger logger = LoggerFactory.getLogger(ProcessesService.class);

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private com.self.dao.service.UserService userDaoService;

    @Autowired
    private ActHiCommentMapper actHiCommentMapper;

    public ResultEntity<PagingResp<ProcessesTodoTaskResp>> getTodoList(PagingReq pagingReq){
        Long userId = CurUserUtils.getUserId();

        PagingResp<ProcessesTodoTaskResp> pagingResp = new PagingResp<>();
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
        List<ProcessesTodoTaskResp> respList = taskList.stream().map(task -> {
            ProcessesTodoTaskResp resp = new ProcessesTodoTaskResp();
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

        Set<Long> userIds = respList.stream().map(ProcessesTodoTaskResp::getTaskAssignee).filter(StringUtils::isNotBlank).map(Long::parseLong).collect(Collectors.toSet());
        Set<Long> applicantUserIds = respList.stream().map(ProcessesTodoTaskResp::getProcessApplicant).filter(StringUtils::isNotBlank).map(Long::parseLong).collect(Collectors.toSet());
        userIds.addAll(applicantUserIds);

        Map<Long, String> userRealNameMap = userDaoService.listByIds(userIds).stream().collect(Collectors.toMap(User::getId, User::getRealName));

        respList.forEach(resp -> resp.setTaskAssigneeRealName(userRealNameMap.getOrDefault(Long.parseLong(resp.getTaskAssignee()), null)));

        respList.forEach(resp -> resp.setProcessApplicantRealName(userRealNameMap.getOrDefault(Long.parseLong(resp.getProcessApplicant()), null)));

        pagingResp.setData(respList);

        return ResultEntity.ok(pagingResp);
    }

    public ResultEntity<List<ProcessesHistoryResp>> getHistoryList(String processInstanceId){
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

        List<ProcessesHistoryResp> respList = Lists.newArrayList();

        for (HistoricActivityInstance activity : activities) {
            ProcessesHistoryResp resp = new ProcessesHistoryResp();

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

    public ResultEntity<PagingResp<ProcessesDoneTaskResp>> getDoneList(PagingReq pagingReq){
        Long userId = CurUserUtils.getUserId();

        PagingResp<ProcessesDoneTaskResp> pagingResp = new PagingResp<>();
        pagingResp.setCurrentPage(pagingReq.getCurrentPage());
        pagingResp.setPageSize(pagingReq.getPageSize());

        int startIndex = ((pagingReq.getCurrentPage() - 1) * pagingReq.getPageSize());

        HistoricTaskInstanceQuery historicTaskInstanceQuery = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(userId.toString())
                .finished()
                .orderByHistoricTaskInstanceEndTime()
                .desc();

        long total = historicTaskInstanceQuery.count();
        pagingResp.setTotalRecord(total);

        long totalPage = (total + pagingReq.getPageSize() - 1) / pagingReq.getPageSize();
        pagingResp.setTotalPage((int) totalPage);

        if(startIndex >= total){
            pagingResp.setData(Lists.newArrayListWithCapacity(0));
            return ResultEntity.ok(pagingResp);
        }

        List<HistoricTaskInstance> historicTaskList = historicTaskInstanceQuery.listPage(startIndex, pagingReq.getPageSize());

        Set<String> processInstanceIds = historicTaskList.stream()
                .map(HistoricTaskInstance::getProcessInstanceId)
                .collect(Collectors.toSet());

        List<HistoricProcessInstance> processInstanceList = Lists.newArrayList();
        if(!CollectionUtils.isEmpty(processInstanceIds)){
            processInstanceList = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceIds(processInstanceIds)
                    .list();
        }
        Map<String, String> businessKeyMap = processInstanceList.stream().collect(Collectors.toMap(
                HistoricProcessInstance::getId,
                process -> (process.getBusinessKey() == null ? "" : process.getBusinessKey())
        ));

        List<ActHiComment> commentList = actHiCommentMapper.selectBatchByProcessInstanceIds(new ArrayList<>(processInstanceIds));
        Map<String, String> commentMap = commentList.stream().collect(Collectors.toMap(ActHiComment::getTaskId, ActHiComment::getMessage));

        List<ProcessesDoneTaskResp> respList = historicTaskList.stream().map(historicTask -> {
            ProcessesDoneTaskResp resp = new ProcessesDoneTaskResp();
            resp.setTaskId(historicTask.getId());
            resp.setTaskAssignee(historicTask.getAssignee());
            resp.setTaskActivityId(historicTask.getTaskDefinitionKey());
            resp.setTaskActivityName(historicTask.getName());
            resp.setStartTime(historicTask.getCreateTime());
            resp.setEndTime(historicTask.getEndTime());
            resp.setDurationInMillis(historicTask.getDurationInMillis());
            resp.setProcessInstanceId(historicTask.getProcessInstanceId());

            List<String> splitStr = Arrays.asList(org.apache.commons.lang3.StringUtils.split(historicTask.getProcessDefinitionId(), CommonConstants.STR_COLON));
            resp.setProcessInstanceKey(splitStr.get(0));

            return resp;
        }).collect(Collectors.toList());

        Set<Long> userIds = respList.stream().map(ProcessesDoneTaskResp::getTaskAssignee).filter(StringUtils::isNotBlank).map(Long::parseLong).collect(Collectors.toSet());
        Map<Long, String> userRealNameMap = userDaoService.listByIds(userIds).stream().collect(Collectors.toMap(User::getId, User::getRealName));

        respList.forEach(resp -> resp.setTaskAssigneeRealName(userRealNameMap.getOrDefault(Long.parseLong(resp.getTaskAssignee()), null)));

        respList.forEach(resp -> resp.setBusinessKey(businessKeyMap.getOrDefault(resp.getProcessInstanceId(), null)));

        respList.forEach(resp -> resp.setComment(commentMap.getOrDefault(resp.getTaskId(), null)));

        pagingResp.setData(respList);

        return ResultEntity.ok(pagingResp);
    }

    public InputStream generateHighLightDiagram(String processInstanceId){
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();

        String processDefinitionId = null;
        //高亮节点
        List<String> highLightedActivities = Lists.newArrayList();
        //高亮连线
        List<String> highLightedFlows = Lists.newArrayList();

        if(Objects.nonNull(processInstance)){
            //实例运行中
            processDefinitionId = processInstance.getProcessDefinitionId();
            highLightedActivities = runtimeService.getActiveActivityIds(processInstanceId);
        }else{
            //实例已结束
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();
            processDefinitionId = historicProcessInstance.getProcessDefinitionId();
        }

        //查询已经过节点和连线
        List<HistoricActivityInstance> historicList = historyService
                .createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .list();

        for (HistoricActivityInstance his : historicList) {
            if (BpmnXMLConstants.ELEMENT_SEQUENCE_FLOW.equals(his.getActivityType())) {
                //连线
                highLightedFlows.add(his.getActivityId());
            } else {
                //节点
                highLightedActivities.add(his.getActivityId());
            }
        }

        //查询BPMN模型
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

        //渲染
        DefaultProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();

        return generator.generateDiagram(
                bpmnModel,
                "png",  //图片类型
                highLightedActivities,  //高亮节点
                highLightedFlows,  //高亮连线
                "宋体",  //节点字体
                "宋体",  //连线标签字体
                "宋体",  //注释字体
                null,  //类加载器
                1.0,  //缩放因子
                true  //未设置标签时是否绘制连线名
        );
    }

    public ResultEntity<Deployment> deploy(String bpmnFileName, String deployName){
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("processes/" + bpmnFileName)
                .name(deployName)
                .deploy();

        return ResultEntity.ok(deployment);
    }

}
