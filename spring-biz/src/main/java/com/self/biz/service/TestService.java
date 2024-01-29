package com.self.biz.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.exception.ExcelAnalysisException;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.self.biz.excel.listener.test.TestListener;
import com.self.biz.kafka.producer.test.RecordKafkaProducer;
import com.self.biz.request.TxWeatherRequest;
import com.self.common.api.condition.test.TestListCondition;
import com.self.common.api.export.test.TestExport;
import com.self.common.api.importo.test.TestImport;
import com.self.common.api.req.delayqueue.TestDelayQueueReq;
import com.self.common.api.req.job.*;
import com.self.common.api.req.kafka.TestKafkaReq;
import com.self.common.api.req.test.TestAddReq;
import com.self.common.api.req.test.TestListReq;
import com.self.common.api.resp.test.TestListResp;
import com.self.common.api.resp.test.TestSensitiveResp;
import com.self.common.converter.ToStringConverterFactory;
import com.self.common.domain.ResultEntity;
import com.self.common.enums.DeletedEnum;
import com.self.common.enums.RedisDelayQueueEnum;
import com.self.common.exception.BizException;
import com.self.common.exception.HttpException;
import com.self.common.exception.ParamException;
import com.self.common.utils.*;
import com.self.dao.api.page.PagingResp;
import com.self.dao.entity.Test;
import com.self.dao.mapper.TestMapper;
import com.self.quartz.job.TestJob;
import com.self.quartz.service.QuartzService;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.multipart.MultipartFile;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

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

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private LocalValidatorFactoryBean validator;

    @Autowired
    private RedissonUtils redissonUtils;

    public ResultEntity<String> testReq(String req){
        return ResultEntity.ok("testKey:" + req);
    }

    public ResultEntity<String> testFormUrlEncoded(String param1, String param2){
        return ResultEntity.ok("x-www-form-urlencoded:" + param1 + "," + param2);
    }

    public <K, V> ResultEntity<List<V>> testFunction(K req, Function<K, List<V>> function){
        return ResultEntity.ok(function.apply(req));
    }

    public List<String> testFunctionLogic(String params){
        return Collections.singletonList(params);
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
        TestListCondition condition = BeanUtils.copyProperties(testListReq, TestListCondition.class);

        PageInfo<Test> pageInfo = PageHelper.startPage(testListReq.getCurrentPage(), testListReq.getPageSize()).doSelectPageInfo(() -> testMapper.selectAllByNameTestList(condition));

        List<TestListResp> respList = BeanUtils.copyList(pageInfo.getList(), TestListResp.class);
        respList.forEach(r -> {
            if(NumberUtils.LONG_ZERO.equals(r.getIsDeleted())){
                r.setIsDeletedName(DeletedEnum.ENABLE.getDesc());
            }else{
                r.setIsDeletedName(DeletedEnum.DELETED.getDesc());
            }
        });

        PageInfo<TestListResp> pageResult = BeanUtils.copyProperties(pageInfo, PageInfo.class);
        pageResult.setList(respList);

        return ResultEntity.ok(new PagingResp<>(pageResult));
    }

    public ResultEntity<Object> testTransaction(TestAddReq testAddReq){
        Test test = BeanUtils.copyProperties(testAddReq, Test.class);

        test.setCreateBy(CurUserUtils.getUserId());
        test.setUpdateBy(CurUserUtils.getUserId());

        transactionTemplate.executeWithoutResult(transactionStatus -> {
            try{
                testMapper.insert(test);

                /**
                 * 事务内部异常会回滚
                 if(test.getId() % 2 != 0){
                 throw new BizException("测试异常");
                 }*/
            }catch (Exception e){
                transactionStatus.setRollbackOnly();
                logger.error("测试事务异常，事务回滚");
            }
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

    public ResultEntity<Object> testImport(MultipartFile file){
        try{
            EasyExcel.read(file.getInputStream(), TestImport.class, new TestListener<TestImport>(validator, testMapper)).sheet().headRowNumber(2).doRead();
        }catch (Exception e){
            String msg = "导入失败";
            if(e instanceof ExcelAnalysisException){
                msg = e.getMessage();
            }
            throw new ParamException(msg);
        }

        return ResultEntity.ok();
    }

    public void testExport(HttpServletResponse response) throws Exception {
        List<TestExport> dataList = Lists.newArrayListWithCapacity(2);
        TestExport testExport1 = TestExport.builder()
                .id(1L).name("名称1").value(1.1).date(new Date()).build();
        dataList.add(testExport1);

        TestExport testExport2 = TestExport.builder()
                .id(2L).name("名称2").value(2.1).date(new Date()).build();
        dataList.add(testExport2);

        ExcelUtils.exportToWeb(response, "测试导出", "sheet1", TestExport.class, dataList);
    }

    public ResultEntity<JSONObject> testRetrofit(){
        Map<String, Object> reqMap = Maps.newHashMap();
        reqMap.put("source", "pc");
        reqMap.put("weather_type", "forecast_24h");
        reqMap.put("province", "重庆市");
        reqMap.put("city", "重庆市");
        reqMap.put("county", "北碚区");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TxWeatherRequest.BASE_URL)
                .client(OkHttpClientUtils.getInstance())
                .addConverterFactory(new ToStringConverterFactory())
                .build();

        TxWeatherRequest txWeatherRequest = retrofit.create(TxWeatherRequest.class);
        Call<String> call = txWeatherRequest.getWeatherInfo(reqMap);

        String result = null;
        try {
            Response<String> resp = call.execute();
            result = Optional.ofNullable(resp).map(Response::body).orElse(null);
        } catch (IOException e) {
            throw new HttpException("腾讯天气请求失败", e);
        }

        return ResultEntity.ok(JSON.parseObject(result));
    }

    public ResultEntity<Object> testKafkaSend(TestKafkaReq testKafkaReq){
        recordKafkaProducer.send(testKafkaReq);

        return ResultEntity.ok();
    }

    public ResultEntity<Object> testDelayQueueSend(TestDelayQueueReq testDelayQueueReq){
        redissonUtils.addDelayQueue(RedisDelayQueueEnum.TEST_DELAY_RECORD_1.getCode(), 10, TimeUnit.SECONDS, testDelayQueueReq.getMsg() + "-1");

        redissonUtils.addDelayQueue(RedisDelayQueueEnum.TEST_DELAY_RECORD_2.getCode(), 20, TimeUnit.SECONDS, testDelayQueueReq.getMsg() + "-2");

        return ResultEntity.ok();
    }

}
