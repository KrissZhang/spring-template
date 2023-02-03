package com.self.biz.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Maps;
import com.self.biz.kafka.producer.test.RecordKafkaProducer;
import com.self.common.api.condition.test.TestListCondition;
import com.self.common.api.req.job.*;
import com.self.common.api.req.kafka.TestKafkaReq;
import com.self.common.api.req.test.TestAddReq;
import com.self.common.api.req.test.TestListReq;
import com.self.common.api.resp.test.TestListResp;
import com.self.common.api.resp.test.TestSensitiveResp;
import com.self.common.domain.ResultEntity;
import com.self.common.enums.DeletedEnum;
import com.self.common.exception.BizException;
import com.self.common.utils.BeanUtils;
import com.self.common.utils.CurUserUtils;
import com.self.common.utils.TransactionUtils;
import com.self.common.utils.http.BaseHttpRequest;
import com.self.common.utils.http.OkHttpUtils;
import com.self.dao.api.page.PagingResp;
import com.self.dao.entity.Test;
import com.self.dao.mapper.TestMapper;
import com.self.quartz.job.TestJob;
import com.self.quartz.service.QuartzService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Autowired
    private RecordKafkaProducer recordKafkaProducer;

    public ResultEntity<String> testReq(String req){
        return ResultEntity.ok("testKey:" + req);
    }

    public ResultEntity<TestSensitiveResp> testSensitive(){
        TestSensitiveResp resp = new TestSensitiveResp();
        resp.setId(1L);
        resp.setChineseName("张瑾瑜");
        resp.setIdCard("500108202202135115");
        resp.setMobilePhone("15020736323");
        resp.setText("既然如何, 了解清楚随机一段废话到底是一种怎么样的存在, 是解决一切问题的关键.问题的关键究竟为何? 经过上述讨论, 在这种困难的抉择下, 本人思来想去, 寝食难安.带着这些问题, 我们来审视一下随机一段废话. 那么, 我们不妨可以这样来想: 本人也是经过了深思熟虑,在每个日日夜夜思考这个问题. 要想清楚, 随机一段废话, 到底是一种怎么样的存在. 生活中, 若随机一段废话出现了, 我们就不得不考");

        return ResultEntity.ok(resp);
    }

    @Transactional(rollbackFor = {Exception.class, Error.class})
    public ResultEntity<Object> testDel(Integer id){
        testMapper.deleteById(id);

        return ResultEntity.ok();
    }

    public ResultEntity<PagingResp<TestListResp>> testPage(TestListReq testListReq){
        Page<TestListResp> page = Page.of(testListReq.getCurrentPage(), testListReq.getPageSize());

        TestListCondition condition = BeanUtils.copyProperties(testListReq, TestListCondition.class);
        List<Test> testList = testMapper.selectAllByNameTestList(page, condition);

        List<TestListResp> respList = BeanUtils.copyList(testList, TestListResp.class);

        respList.forEach(r -> Optional.ofNullable(DeletedEnum.resolve(r.getIsDeleted())).ifPresent(deletedEnum -> r.setIsDeletedName(deletedEnum.getDesc())));

        return ResultEntity.ok(new PagingResp<>(page, respList));
    }

    public ResultEntity<Object> testTransaction(TestAddReq testAddReq){
        Test test = BeanUtils.copyProperties(testAddReq, Test.class);

        test.setCreateBy(CurUserUtils.getUserId());
        test.setUpdateBy(CurUserUtils.getUserId());

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

    public ResultEntity<JSONObject> testOkHttp(){
        String url = "https://wis.qq.com/weather/common";
        Map<String, Object> reqMap = Maps.newHashMap();
        reqMap.put("source", "pc");
        reqMap.put("weather_type", "forecast_24h");
        reqMap.put("province", "重庆市");
        reqMap.put("city", "重庆市");
        reqMap.put("county", "北碚区");

        BaseHttpRequest request = new BaseHttpRequest(url, BaseHttpRequest.Method.GET);
        request.getQueryParams().putAll(reqMap);
        String response = OkHttpUtils.getInstance().sendRequest(request);

        return ResultEntity.ok(JSON.parseObject(response));
    }

    public ResultEntity<Object> testKafkaSend(TestKafkaReq testKafkaReq){
        recordKafkaProducer.send(testKafkaReq);

        return ResultEntity.ok();
    }

}
