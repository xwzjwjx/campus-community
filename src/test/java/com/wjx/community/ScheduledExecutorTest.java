package com.wjx.community;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author wjx
 * @description
 */
public class ScheduledExecutorTest implements Runnable{

    private String jobName = "";

    public ScheduledExecutorTest(String jobName){
        this.jobName = jobName;
    }

    @Override
    public void run() {
        System.out.println("execute:"+jobName);
    }

    public static void main(String[] args) {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);
        long delay1 = 1;
        long period1 = 1;

        executorService.scheduleAtFixedRate(new ScheduledExecutorTest("job1"),delay1,period1, TimeUnit.SECONDS);

        long delay2 = 2;
        long period2 = 2;
        executorService.scheduleWithFixedDelay(new ScheduledExecutorTest("job2"),delay2,period2,TimeUnit.SECONDS);
    }

}
