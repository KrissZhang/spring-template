package com.self.biz.service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Maps;
import com.self.common.api.condition.test.TestListCondition;
import com.self.common.api.req.job.*;
import com.self.common.api.req.test.TestAddReq;
import com.self.common.api.req.test.TestListReq;
import com.self.common.api.resp.test.TestListResp;
import com.self.common.domain.ResultEntity;
import com.self.common.enums.EnableEnum;
import com.self.common.exception.BizException;
import com.self.common.utils.BeanUtils;
import com.self.common.utils.TransactionUtils;
import com.self.dao.api.page.PagingResp;
import com.self.dao.entity.Test;
import com.self.dao.mapper.TestMapper;
import com.self.quartz.job.TestJob;
import com.self.quartz.service.QuartzService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TestService {

    private static final Logger logger = LoggerFactory.getLogger(TestService.class);

    @Autowired
    private TestMapper testMapper;

    @Autowired
    private QuartzService quartzService;

    @Autowired
    private FileService fileService;

    public ResultEntity<String> testReq(String req){
        return ResultEntity.ok("testKey:" + req);
    }

    public ResultEntity<PagingResp<TestListResp>> testPage(TestListReq testListReq){
        Page<TestListResp> page = Page.of(testListReq.getCurrentPage(), testListReq.getPageSize());

        TestListCondition condition = BeanUtils.copyProperties(testListReq, TestListCondition.class);
        List<Test> testList = testMapper.selectAllByNameTestList(page, condition);

        List<TestListResp> respList = BeanUtils.copyList(testList, TestListResp.class);

        respList.forEach(r -> Optional.ofNullable(EnableEnum.resolve(r.getEnable())).ifPresent(enableEnum -> r.setEnableName(enableEnum.getDesc())));

        return ResultEntity.ok(new PagingResp<>(page, respList));
    }

    public ResultEntity<Object> testTransaction(TestAddReq testAddReq){
        Test test = BeanUtils.copyProperties(testAddReq, Test.class);
        test.setEnable(EnableEnum.ENABLE.getValue());

        TransactionUtils.beginTransaction(() -> {
            testMapper.insert(test);

            /**
             * 事务内部异常会回滚
            if(test.getId() % 2 != 0){
                throw new BizException("测试异常");
            }*/
        });

        //事务外部异常不会回滚
        if(test.getId() % 2 != 0){
            throw new BizException("测试异常");
        }

        return ResultEntity.ok();
    }

    public ResultEntity<Object> testCronJobAdd(TestCronJobAddReq testCronJobAddReq) {
        //任务参数
        Map<String, String> paramMap = Maps.newHashMap();
        if(!CollectionUtils.isEmpty(testCronJobAddReq.getParams())){
            paramMap = JSON.parseObject(JSON.toJSONString(testCronJobAddReq.getParams()), Map.class);
        }

        quartzService.addCronJob(testCronJobAddReq.getJName(), testCronJobAddReq.getJGroup(), testCronJobAddReq.getTName(), testCronJobAddReq.getTGroup(), testCronJobAddReq.getCron(), new Date(), TestJob.class, paramMap);

        return ResultEntity.ok();
    }

    public ResultEntity<Object> testSimpleJobAdd(TestSimpleJobAddReq testSimpleJobAddReq){
        //任务参数
        Map<String, String> paramMap = Maps.newHashMap();
        if(!CollectionUtils.isEmpty(testSimpleJobAddReq.getParams())){
            paramMap = JSON.parseObject(JSON.toJSONString(testSimpleJobAddReq.getParams()), Map.class);
        }

        quartzService.addSimpleJob(testSimpleJobAddReq.getJName(), testSimpleJobAddReq.getJGroup(), testSimpleJobAddReq.getTName(), testSimpleJobAddReq.getTGroup(), testSimpleJobAddReq.getIntervalTime(), new Date(), TestJob.class, paramMap);

        return ResultEntity.ok();
    }

    public ResultEntity<Object> testDelayJobAdd(TestDelayJobAddReq testDelayJobAddReq){
        //任务参数
        Map<String, String> paramMap = Maps.newHashMap();
        if(!CollectionUtils.isEmpty(testDelayJobAddReq.getParams())){
            paramMap = JSON.parseObject(JSON.toJSONString(testDelayJobAddReq.getParams()), Map.class);
        }

        quartzService.addDelayJob(testDelayJobAddReq.getJName(), testDelayJobAddReq.getJGroup(), testDelayJobAddReq.getTName(), testDelayJobAddReq.getTGroup(), testDelayJobAddReq.getStartTime(), TestJob.class, paramMap);

        return ResultEntity.ok();
    }

    public ResultEntity<Object> testJobPause(TestJobPauseReq testJobPauseReq) {
        quartzService.pauseJob(testJobPauseReq.getJName(), testJobPauseReq.getJGroup());

        return ResultEntity.ok();
    }

    public ResultEntity<Object> testJobResume(TestJobResumeReq testJobResumeReq) {
        quartzService.resumeJob(testJobResumeReq.getJName(), testJobResumeReq.getJGroup());

        return ResultEntity.ok();
    }

    public ResultEntity<Object> testJobReschedule(TestJobRescheduleReq testJobRescheduleReq) {
        quartzService.rescheduleSimpleJob(testJobRescheduleReq.getTName(), testJobRescheduleReq.getTGroup(), testJobRescheduleReq.getIntervalTime());

        return ResultEntity.ok();
    }

    public ResultEntity<Object> testJobDel(TestJobDelReq testJobDelReq) {
        quartzService.deleteJob(testJobDelReq.getJName(), testJobDelReq.getJGroup());

        return ResultEntity.ok();
    }

    public ResultEntity<String> testUploadFile(MultipartFile multipartFile) throws IOException {
        return ResultEntity.ok(fileService.uploadFile(multipartFile));
    }

    public void testDownloadFile(HttpServletResponse response, String fileId, String fileName){
        fileService.downloadFile(response, fileId, fileName);
    }

}
