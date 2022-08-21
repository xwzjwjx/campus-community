package com.wjx.community;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author wjx
 * @description
 */
public class ScheduledExecutorTest1 implements Runnable{

    private String jobName = "";

    public ScheduledExecutorTest1(String jobName){
        this.jobName = jobName;
    }

    @Override
    public void run() {
        System.out.println("时间："+new Date()+","+"executor:"+jobName);
    }

    /**
     * 获取最近需要执行任务的时间
     * @param currentDate
     * @param dayOfWeek
     * @param hourOfDay
     * @param minuteOfHour
     * @param secondOfMin
     * @return
     */
    public Calendar getEarlyDate(Calendar currentDate,int dayOfWeek,int hourOfDay,
                                 int minuteOfHour,int secondOfMin){
        int currentWeekOfYear = currentDate.get(Calendar.WEEK_OF_YEAR);
        int currentDayOfWeek = currentDate.get(Calendar.DAY_OF_WEEK);
        int currentHourOfDay = currentDate.get(Calendar.HOUR_OF_DAY);
        int currentMinuteOfHour = currentDate.get(Calendar.MINUTE);
        int currentSecondOfMin = currentDate.get(Calendar.SECOND);
        //是否要推迟
        boolean weekLater = false;
        if (dayOfWeek < currentDayOfWeek) {
            weekLater = true;
        } else if (dayOfWeek == currentDayOfWeek) {
            //当输入条件与当前日期的dayOfWeek相等时，如果输入条件中的
            //hourOfDay小于当前日期的
            //currentHour，则WEEK_OF_YEAR需要推迟一周
            if (hourOfDay < currentHourOfDay) {
                weekLater = true;
            } else if (hourOfDay == currentHourOfDay) {
                //当输入条件与当前日期的dayOfWeek, hourOfDay相等时，
                //如果输入条件中的minuteOfHour小于当前日期的
                //currentMinute，则WEEK_OF_YEAR需要推迟一周
                if (minuteOfHour < currentMinuteOfHour) {
                    weekLater = true;
                } else if (minuteOfHour == currentMinuteOfHour) {
                    //当输入条件与当前日期的dayOfWeek, hourOfDay，
                    //minuteOfHour相等时，如果输入条件中的
                    //secondOfMinite小于当前日期的currentSecond，
                    //则WEEK_OF_YEAR需要推迟一周
                    if (secondOfMin < currentSecondOfMin) {
                        weekLater = true;
                    }
                }
            }
        }
        if (weekLater){
            //设置当前日期中的WEEK_OF_YEAR为当前周推迟一周
            currentDate.set(Calendar.WEEK_OF_YEAR, currentWeekOfYear + 1);
        }
        // 设置当前日期中的DAY_OF_WEEK,HOUR_OF_DAY,MINUTE,SECOND为输入条件中的值。
        currentDate.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        currentDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
        currentDate.set(Calendar.MINUTE, minuteOfHour);
        currentDate.set(Calendar.SECOND, secondOfMin);
        return currentDate;
    }

    public static void main(String[] args) {
        ScheduledExecutorTest1 job = new ScheduledExecutorTest1("job");
        Calendar currentDate = Calendar.getInstance();
        long currentDateLong = currentDate.getTime().getTime();
        System.out.println("currentDate"+currentDate.getTime().toString());
        //计算满足条件的最近一次执行时间
        Calendar earliestDate = job
                .getEarlyDate(currentDate, 3, 16, 00, 00);
        long earliestDateLong = earliestDate.getTime().getTime();
        System.out.println("Earliest Date = "
                + earliestDate.getTime().toString());
        //计算从当前时间到最近一次执行时间的时间间隔
        long delay = earliestDateLong - currentDateLong;
        //计算执行周期为一星期
        long period = 7 * 24 * 60 * 60 * 1000;
        ScheduledExecutorService service = Executors.newScheduledThreadPool(10);
        //从现在开始delay毫秒之后，每隔一星期执行一次job1
        service.scheduleAtFixedRate(job, delay, period,
                TimeUnit.MILLISECONDS);
    }


}
