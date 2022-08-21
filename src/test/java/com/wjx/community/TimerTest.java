package com.wjx.community;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author wjx
 * @description
 */
public class TimerTest extends TimerTask {

    private String jobName = "";

    public TimerTest(String jobName){
        this.jobName = jobName;
    }

    @Override
    public void run() {
        System.out.println("execute:"+jobName);
    }


    public static void main(String[] args) {
        Timer timer = new Timer();
        //从现在开始一秒钟之后每隔一秒执行一次job1
        long delay1 = 1000;
        long period1 = 1000;
        timer.schedule(new TimerTest("job1"),delay1,period1);
        long delay2 = 2*1000;
        long period2 = 2000;
        timer.schedule(new TimerTest("job2"),delay2,period2);
    }
}
