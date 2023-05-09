package com.rdjob.core.executor.scheduling;

import com.rdjob.core.executor.SchedulingExecutor;
import com.rdjob.core.job.RandomJob;
import com.rdjob.core.util.DateTimeUtil;
import org.quartz.*;
import org.springframework.util.StringUtils;


import java.time.LocalDateTime;

/**
 * @description:
 * @author: ChenDong
 * @time: 2022/4/26 20:59
 */
public class CronSchedulingExecutor implements SchedulingExecutor {

    private String cronExpression;
    private Scheduler scheduler;
    private TriggerKey triggerKey;
    private JobDetail jobDetail;
    private Trigger trigger;
    private RandomJob executorJob;
    private String groupName;
    private boolean startNow = false;

    public CronSchedulingExecutor scheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
        return this;
    }

    public CronSchedulingExecutor groupName(String groupName) {
        if (StringUtils.isEmpty(groupName)) {
            this.groupName = "default";
        } else {
            this.groupName = groupName;
        }
        return this;
    }

    public CronSchedulingExecutor executorJob(RandomJob executorJob) {
        this.executorJob = executorJob;
        return this;
    }

    public static CronSchedulingExecutor createCronSchedulingExecutor() {
        return new CronSchedulingExecutor();
    }

    public CronSchedulingExecutor cronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
        return this;
    }


    public CronSchedulingExecutor build() {
        if (StringUtils.isEmpty(this.cronExpression)) {
            throw new IllegalArgumentException("使用CronSchedulingExecutor时必须重写cron表达式");
        }

        triggerKey = new TriggerKey(executorJob.getClass().getName(), groupName);
        jobDetail = JobBuilder.newJob(executorJob.getClass())
                .withIdentity(executorJob.getClass().getName(), groupName)
                .build();
        //创建Cron周期的执行器
        CronScheduleBuilder schedulerBuilder = CronScheduleBuilder
                .cronSchedule(this.cronExpression)
                .withMisfireHandlingInstructionDoNothing();
        trigger = TriggerBuilder.newTrigger()
                //trigger名字
                .withIdentity(executorJob.getClass().getName(), groupName)
                //.startNow()
                //开始执行时间
                //.startAt(this.executorJobStartTime)
                //开始结束时间
                //.endAt(executorEndTime)
                //执行器
                .withSchedule(schedulerBuilder)
                .build();
        return this;
    }

    @Override
    public void start() throws SchedulerException {
        if (!scheduler.checkExists(triggerKey)) {
            //执行
            scheduler.scheduleJob(jobDetail, trigger);
            scheduler.start();
        } else {
            //刷新时间周期
            scheduler.rescheduleJob(triggerKey, trigger);
        }
    }

    @Override
    public void run() throws SchedulerException {
        //2s后执行
        this.cronExpression = DateTimeUtil.format(LocalDateTime.now().plusSeconds(2), "ss mm HH dd MM ?");
        this.build();
        this.start();
    }

    @Override
    public void stop() throws SchedulerException {
        scheduler.interrupt(getJobKey());
    }

    @Override
    public void delete() throws SchedulerException {
        scheduler.deleteJob(getJobKey());
    }

    @Override
    public void pause() throws SchedulerException {
        scheduler.pauseJob(getJobKey());
    }

    @Override
    public void resume() throws SchedulerException {
        scheduler.resumeJob(getJobKey());
    }

    @Override
    public JobKey getJobKey() throws SchedulerException {
        JobKey key = this.jobDetail.getKey();
        if (scheduler.checkExists(key)) {
            return key;
        }
        throw new IllegalArgumentException("不存在的JobKey: " + key.toString());
    }
}
