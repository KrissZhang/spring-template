package com.self.quartz.job;

import com.alibaba.fastjson.JSON;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 测试任务
 */
public class TestJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(TestJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("====== TestJob开始执行 ======");

        try{
            JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
            //TODO
            logger.info("jobDataMap：" + JSON.toJSONString(jobDataMap));
        }catch (Exception e){
            logger.error("TestJob执行异常：", e);
        }

        logger.info("====== TestJob执行结束 ======");
    }

}
