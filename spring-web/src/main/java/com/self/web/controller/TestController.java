package com.self.web.controller;

import com.alibaba.fastjson2.JSONObject;
import com.self.biz.service.TestService;
import com.self.common.annotation.OperLog;
import com.self.common.annotation.RateLimiter;
import com.self.common.api.req.job.*;
import com.self.common.api.req.kafka.TestKafkaReq;
import com.self.common.api.req.test.TestAddReq;
import com.self.common.api.req.test.TestListReq;
import com.self.common.api.resp.test.TestListResp;
import com.self.common.api.resp.test.TestSensitiveResp;
import com.self.common.constants.ApiURI;
import com.self.common.domain.ResultEntity;
import com.self.common.enums.BusinessTypeEnum;
import com.self.common.enums.LimitTypeEnum;
import com.self.dao.api.page.PagingResp;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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

    @Operation(summary = "测试x-www-form-urlencoded请求")
    @RateLimiter(limitType = LimitTypeEnum.IP, count = 10)
    @OperLog(title = "测试x-www-form-urlencoded请求", businessType = BusinessTypeEnum.OTHER)
    @PostMapping(value = ApiURI.TEST_FORM_URLENCODED_REQ)
    public ResultEntity<String> testFormUrlEncoded(@Parameter(description = "请求参数1") @RequestParam String param1, @Parameter(description = "请求参数2") @RequestParam String param2){
        return testService.testFormUrlEncoded(param1, param2);
    }

    @Operation(summary = "测试脱敏")
    @RateLimiter(limitType = LimitTypeEnum.IP, count = 10)
    @OperLog(title = "测试脱敏", businessType = BusinessTypeEnum.OTHER)
    @GetMapping(value = ApiURI.TEST_SENSITIVE)
    public ResultEntity<TestSensitiveResp> testSensitive(){
        return testService.testSensitive();
    }

    @Operation(summary = "测试逻辑删除")
    @RateLimiter(limitType = LimitTypeEnum.IP, count = 10)
    @OperLog(title = "测试逻辑删除", businessType = BusinessTypeEnum.DELETE)
    @DeleteMapping(value = ApiURI.TEST_DEL)
    public ResultEntity<Object> testDel(@Parameter(description = "删除主键id") @RequestParam Integer id){
        return testService.testDel(id);
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

    @Operation(summary = "测试添加延迟执行任务")
    @OperLog(title = "测试添加延迟执行任务", businessType = BusinessTypeEnum.ADD)
    @PostMapping(value = ApiURI.TEST_DELAY_JOB_ADD)
    public ResultEntity<Object> testDelayJobAdd(@RequestBody @Validated TestDelayJobAddReq testDelayJobAddReq) {
        return testService.testDelayJobAdd(testDelayJobAddReq);
    }

    @Operation(summary = "测试暂停定时任务")
    @OperLog(title = "测试暂停定时任务", businessType = BusinessTypeEnum.EDIT)
    @PostMapping(value = ApiURI.TEST_JOB_PAUSE)
    public ResultEntity<Object> testJobPause(@RequestBody @Validated TestJobPauseReq testJobPauseReq) {
        return testService.testJobPause(testJobPauseReq);
    }

    @Operation(summary = "测试恢复定时任务")
    @OperLog(title = "测试恢复定时任务", businessType = BusinessTypeEnum.EDIT)
    @PostMapping(value = ApiURI.TEST_JOB_RESUME)
    public ResultEntity<Object> testJobResume(@RequestBody @Validated TestJobResumeReq testJobResumeReq) {
        return testService.testJobResume(testJobResumeReq);
    }

    @Operation(summary = "测试重启定时任务")
    @OperLog(title = "测试重启定时任务", businessType = BusinessTypeEnum.EDIT)
    @PostMapping(value = ApiURI.TEST_JOB_RESCHEDULE)
    public ResultEntity<Object> testJobReschedule(@RequestBody @Validated TestJobRescheduleReq testJobRescheduleReq) {
        return testService.testJobReschedule(testJobRescheduleReq);
    }

    @Operation(summary = "测试删除定时任务")
    @OperLog(title = "测试删除定时任务", businessType = BusinessTypeEnum.DELETE)
    @PostMapping(value = ApiURI.TEST_JOB_DEL)
    public ResultEntity<Object> testJobDel(@RequestBody @Validated TestJobDelReq testJobDelReq) {
        return testService.testJobDel(testJobDelReq);
    }

    @Operation(summary = "测试上传文件")
    @OperLog(title = "测试上传文件", businessType = BusinessTypeEnum.OTHER)
    @PostMapping(value = ApiURI.TEST_UPLOAD_FILE)
    public ResultEntity<String> testUploadFile(MultipartFile multipartFile) throws IOException {
        return testService.testUploadFile(multipartFile);
    }

    @Operation(summary = "测试下载文件")
    @OperLog(title = "测试下载文件", businessType = BusinessTypeEnum.OTHER)
    @PostMapping(value = ApiURI.TEST_DOWNLOAD_FILE)
    public void testDownloadFile(HttpServletResponse response, @RequestParam String fileId, @RequestParam String fileName) {
        testService.testDownloadFile(response, fileId, fileName);
    }

    @Operation(summary = "测试导入")
    @OperLog(title = "测试导入", businessType = BusinessTypeEnum.IMPORT)
    @PostMapping(value = ApiURI.TEST_IMPORT)
    public ResultEntity<Object> testImport(MultipartFile file) {
        return testService.testImport(file);
    }

    @Operation(summary = "测试导出")
    @OperLog(title = "测试导出", businessType = BusinessTypeEnum.EXPORT)
    @PostMapping(value = ApiURI.TEST_EXPORT)
    public void testExport(HttpServletResponse response) throws Exception {
        testService.testExport(response);
    }

    @Operation(summary = "测试Retrofit")
    @RateLimiter(limitType = LimitTypeEnum.IP, count = 10)
    @OperLog(title = "测试Retrofit", businessType = BusinessTypeEnum.OTHER)
    @GetMapping(value = ApiURI.TEST_RETROFIT)
    public ResultEntity<JSONObject> testRetrofit(){
        return testService.testRetrofit();
    }

    @Operation(summary = "测试Kafka消息")
    @OperLog(title = "测试Kafka消息", businessType = BusinessTypeEnum.OTHER)
    @PostMapping(value = ApiURI.TEST_KAFKA_SEND)
    public ResultEntity<Object> testKafkaSend(@RequestBody @Validated TestKafkaReq testKafkaReq){
        return testService.testKafkaSend(testKafkaReq);
    }

}
