package com.self.quartz.service;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class QuartzService {

    private static final Logger logger = LoggerFactory.getLogger(QuartzService.class);

    @Autowired
    private Scheduler scheduler;

    /**
     * 新增定时任务
     * @param jName 任务名称
     * @param jGroup 任务组
     * @param tName 触发器名称
     * @param tGroup 触发器组
     * @param cron cron表达式
     * @param startTime 任务开始时间
     * @param clazz 任务
     */
    public void addCronJob(String jName, String jGroup, String tName, String tGroup, String cron, Date startTime, Class<? extends Job> clazz, Map<String, String> paramMap){
        try{
            //JobDataMap
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.putAll(paramMap);

            // JobDetail
            JobDetail jobDetail = JobBuilder.newJob(clazz)
                    .setJobData(jobDataMap)
                    .withIdentity(jName, jGroup)
                    .build();

            // Trigger
            CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                    .withIdentity(tName, tGroup)
                    .startAt(startTime)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                    .build();

            // 启动调度器
            scheduler.start();
            scheduler.scheduleJob(jobDetail, cronTrigger);
        }catch (Exception e){
            logger.error("创建定时任务失败：", e);
        }
    }

    /**
     * 新增定时任务
     * @param jName 任务名称
     * @param jGroup 任务组
     * @param tName 触发器名称
     * @param tGroup 触发器组
     * @param intervalTime 间隔时间(秒)
     * @param startTime 任务开始时间
     * @param clazz 任务
     */
    public void addSimpleJob(String jName, String jGroup, String tName, String tGroup, Integer intervalTime, Date startTime, Class<? extends Job> clazz, Map<String, String> paramMap){
        try{
            //JobDataMap
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.putAll(paramMap);

            // JobDetail
            JobDetail jobDetail = JobBuilder.newJob(clazz)
                    .setJobData(jobDataMap)
                    .withIdentity(jName, jGroup)
                    .build();

            // Trigger
            SimpleTrigger simpleTrigger = TriggerBuilder.newTrigger()
                    .withIdentity(tName, tGroup)
                    .startAt(startTime)
                    .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(intervalTime))
                    .build();

            // 启动调度器
            scheduler.start();
            scheduler.scheduleJob(jobDetail, simpleTrigger);
        }catch (Exception e){
            logger.error("创建定时任务失败：", e);
        }
    }

    /**
     * 暂停定时任务
     * @param jName 任务名称
     * @param jGroup 任务组
     * @throws SchedulerException SchedulerException
     */
    public void pauseJob(String jName, String jGroup) throws SchedulerException {
        scheduler.pauseJob(JobKey.jobKey(jName, jGroup));
    }

    /**
     * 恢复定时任务
     * @param jName 任务名称
     * @param jGroup 任务组
     * @throws SchedulerException SchedulerException
     */
    public void resumeJob(String jName, String jGroup) throws SchedulerException {
        scheduler.resumeJob(JobKey.jobKey(jName, jGroup));
    }

    /**
     * 重启定时任务
     * @param tName 触发器名称
     * @param tGroup 触发器组
     * @param cron 表达式
     * @throws SchedulerException SchedulerException
     */
    public void rescheduleCronJob(String tName, String tGroup, String cron) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(tName, tGroup);

        // 表达式调度构建器
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        CronTrigger cronTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);

        // 按新的 cronExpression 表达式重新构建 trigger
        cronTrigger = cronTrigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();

        // 按新的 trigger 重新设置 job 执行，重启触发器
        scheduler.rescheduleJob(triggerKey, cronTrigger);
    }

    /**
     * 重启定时任务
     * @param tName 触发器名称
     * @param tGroup 触发器组
     * @param intervalTime 间隔时间(秒)
     * @throws SchedulerException SchedulerException
     */
    public void rescheduleSimpleJob(String tName, String tGroup, Integer intervalTime) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(tName, tGroup);

        // 时间间隔调度构建器
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.repeatSecondlyForever(intervalTime);
        SimpleTrigger simpleTrigger = (SimpleTrigger) scheduler.getTrigger(triggerKey);

        // 按新的 间隔时间 重新构建 trigger
        simpleTrigger = simpleTrigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();

        // 按新的 trigger 重新设置 job 执行，重启触发器
        scheduler.rescheduleJob(triggerKey, simpleTrigger);
    }

    /**
     * 删除任务
     * @param jName 任务名称
     * @param jGroup 任务组
     * @throws SchedulerException SchedulerException
     */
    public void deleteJob(String jName, String jGroup) throws SchedulerException {
        scheduler.pauseTrigger(TriggerKey.triggerKey(jName, jGroup));
        scheduler.unscheduleJob(TriggerKey.triggerKey(jName, jGroup));
        scheduler.deleteJob(JobKey.jobKey(jName, jGroup));
    }

}
