package com.self.web.controller;

import com.self.biz.service.TestService;
import com.self.common.annotation.OperLog;
import com.self.common.annotation.RateLimiter;
import com.self.common.api.req.job.*;
import com.self.common.api.req.test.TestAddReq;
import com.self.common.api.req.test.TestListReq;
import com.self.common.api.resp.test.TestListResp;
import com.self.common.constants.ApiURI;
import com.self.common.domain.ResultEntity;
import com.self.common.enums.BusinessTypeEnum;
import com.self.common.enums.LimitTypeEnum;
import com.self.dao.api.page.PagingResp;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "测试管理")
@RestController
public class TestController {

    @Autowired
    private TestService testService;

    @Operation(summary = "测试请求")
    @RateLimiter(limitType = LimitTypeEnum.IP, count = 10)
    @OperLog(title = "测试请求", businessType = BusinessTypeEnum.OTHER)
    @GetMapping(value = ApiURI.TEST_REQ)
    public ResultEntity<String> testReq(@Parameter(description = "请求参数") @RequestParam String req){
        return testService.testReq(req);
    }

    @Operation(summary = "测试分页")
    @OperLog(title = "测试分页", businessType = BusinessTypeEnum.OTHER)
    @PostMapping(value = ApiURI.TEST_PAGE)
    public ResultEntity<PagingResp<TestListResp>> testPage(@RequestBody @Validated TestListReq testListReq){
        return testService.testPage(testListReq);
    }

    @Operation(summary = "测试事务")
    @OperLog(title = "测试事务", businessType = BusinessTypeEnum.ADD)
    @PostMapping(value = ApiURI.TEST_TRANSACTION)
    public ResultEntity<Object> testTransaction(@RequestBody @Validated TestAddReq testAddReq){
        return testService.testTransaction(testAddReq);
    }

    @Operation(summary = "测试添加Cron定时任务")
    @OperLog(title = "测试添加Cron定时任务", businessType = BusinessTypeEnum.ADD)
    @PostMapping(value = ApiURI.TEST_CRON_JOB_ADD)
    public ResultEntity<Object> testCronJobAdd(@RequestBody @Validated TestCronJobAddReq testCronJobAddReq) {
        return testService.testCronJobAdd(testCronJobAddReq);
    }

    @Operation(summary = "测试添加时间间隔定时任务")
    @OperLog(title = "测试添加时间间隔定时任务", businessType = BusinessTypeEnum.ADD)
    @PostMapping(value = ApiURI.TEST_SIMPLE_JOB_ADD)
    public ResultEntity<Object> testSimpleJobAdd(@RequestBody @Validated TestSimpleJobAddReq testSimpleJobAddReq) {
        return testService.testSimpleJobAdd(testSimpleJobAddReq);
    }

    @Operation(summary = "测试暂停定时任务")
    @OperLog(title = "测试暂停定时任务", businessType = BusinessTypeEnum.EDIT)
    @PostMapping(value = ApiURI.TEST_JOB_PAUSE)
    public ResultEntity<Object> testJobPause(@RequestBody @Validated TestJobPauseReq testJobPauseReq) throws SchedulerException {
        return testService.testJobPause(testJobPauseReq);
    }

    @Operation(summary = "测试恢复定时任务")
    @OperLog(title = "测试恢复定时任务", businessType = BusinessTypeEnum.EDIT)
    @PostMapping(value = ApiURI.TEST_JOB_RESUME)
    public ResultEntity<Object> testJobResume(@RequestBody @Validated TestJobResumeReq testJobResumeReq) throws SchedulerException {
        return testService.testJobResume(testJobResumeReq);
    }

    @Operation(summary = "测试重启定时任务")
    @OperLog(title = "测试重启定时任务", businessType = BusinessTypeEnum.EDIT)
    @PostMapping(value = ApiURI.TEST_JOB_RESCHEDULE)
    public ResultEntity<Object> testJobReschedule(@RequestBody @Validated TestJobRescheduleReq testJobRescheduleReq) throws SchedulerException {
        return testService.testJobReschedule(testJobRescheduleReq);
    }

    @Operation(summary = "测试删除定时任务")
    @OperLog(title = "测试删除定时任务", businessType = BusinessTypeEnum.DELETE)
    @PostMapping(value = ApiURI.TEST_JOB_DEL)
    public ResultEntity<Object> testJobDel(@RequestBody @Validated TestJobDelReq testJobDelReq) throws SchedulerException {
        return testService.testJobDel(testJobDelReq);
    }

}
