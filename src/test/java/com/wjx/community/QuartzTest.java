package com.wjx.community;

import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wjx
 * @description
 */
public class QuartzTest implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("Generating report - "
                + jobExecutionContext.getFireTime().toString()+ ", type ="
                + jobExecutionContext.getJobDetail().getJobDataMap().get("key00"));
        System.out.println(new Date().toString());
    }

    public static void main(String[] args) throws SchedulerException {
        StdSchedulerFactory schedFact = new StdSchedulerFactory();
        Scheduler scheduler = schedFact.getScheduler();
        scheduler.start();
        Map<String,String> jobData = new HashMap<>();
        String jobName = "myJob";
        String jobGroup = "myJobGroup";
        jobData.put("key00","value00");
        //创建job
        JobDetail jobDetail = JobBuilder.newJob(QuartzTest.class)
                .withIdentity(jobName,jobGroup)
                .usingJobData("key01","value01")
                .usingJobData(new JobDataMap(jobData))
                .storeDurably()
                .build();
        //创建trigger
        String triggerName = "myTrigger";
        String triggerGroup = "myTriggerGroup";
        String jobTime = "0 24 20 ? * 3";
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerName,triggerGroup)
                .startAt(DateBuilder.futureDate(1, DateBuilder.IntervalUnit.SECOND))
                .withSchedule(CronScheduleBuilder.cronSchedule(jobTime))
                .startNow()
                .build();
        scheduler.scheduleJob(jobDetail,trigger);

    }
}
