package com.self.biz.processes.listener;

import com.self.common.utils.SpringUtils;
import com.self.dao.entity.User;
import org.flowable.engine.TaskService;
import org.flowable.task.service.delegate.DelegateTask;
import org.flowable.task.service.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 流程任务监听器
 */
@Component("hrAssigneeListener")
public class HrAssigneeListener implements TaskListener {

    @Autowired
    private com.self.biz.service.UserService userLogicService;

    @Override
    public void notify(DelegateTask delegateTask) {
        //取流程变量(如：申请人)
        Long applicant = (Long) delegateTask.getVariable("applicant");

        //获取审批HR - TODO
        User hrUser = userLogicService.selectUserByUserName("user3");

        //设置审批HR
        SpringUtils.getBean(TaskService.class)
                        .setAssignee(delegateTask.getId(), hrUser == null ? null : hrUser.getId().toString());
    }

}
