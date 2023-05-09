package com.rdjob.core.executor.scheduling;

import com.rdjob.core.executor.SchedulingExecutor;
import com.rdjob.core.job.RandomJob;
import com.rdjob.core.util.DateTimeUtil;
import org.quartz.*;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @description:
 * @author: ChenDong
 * @time: 2022/4/13 16:30
 */
public class DefaultSchedulingExecutor implements SchedulingExecutor {

    private Scheduler scheduler;
    private Integer randomSeconds;
    private RandomJob executorJob;

    private Date executorJobStartTime;

    private JobDetail jobDetail;
    private TriggerKey triggerKey;
    private String groupName;
    private Trigger trigger;
    private JobDataMap jobData;

    public DefaultSchedulingExecutor scheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
        return this;
    }

    public DefaultSchedulingExecutor randomSeconds(Integer randomSeconds) {
        this.randomSeconds = randomSeconds;
        return this;
    }

    public DefaultSchedulingExecutor executorJob(RandomJob executorJob) {
        this.executorJob = executorJob;
        return this;
    }

    public static DefaultSchedulingExecutor createDefaultSchedulingExecutor() {
        return new DefaultSchedulingExecutor();
    }

    public DefaultSchedulingExecutor executorJobStartTime(Date executorJobStartTime) {
        this.executorJobStartTime = executorJobStartTime;
        return this;
    }

    public DefaultSchedulingExecutor groupName(String groupName) {
        if (StringUtils.isEmpty(groupName)) {
            this.groupName = "default";
        } else {
            this.groupName = groupName;
        }
        return this;
    }

    public DefaultSchedulingExecutor jobData(JobDataMap jobDataMap) {
        this.jobData = jobDataMap;
        return this;
    }

    public DefaultSchedulingExecutor build() {
        triggerKey = new TriggerKey(executorJob.getClass().getName(), groupName);
        JobBuilder jobBuilder = JobBuilder.newJob(executorJob.getClass())
                .withIdentity(executorJob.getClass().getName(), groupName);
        if (jobData != null) {
            jobBuilder.setJobData(jobData);
        }
        jobDetail = jobBuilder.build();

        //创建时间周期的执行器
        DailyTimeIntervalScheduleBuilder schedulerBuilder = DailyTimeIntervalScheduleBuilder
                .dailyTimeIntervalSchedule()
                //忽略立即启动执行
                .withMisfireHandlingInstructionDoNothing()
                //重复执行次数
                .withRepeatCount(0)
                //执行周期 必须要指定，不然会按默认1分钟执行周期
                .withInterval(1, DateBuilder.IntervalUnit.SECOND);
        trigger = TriggerBuilder.newTrigger()
                //trigger名字
                .withIdentity(executorJob.getClass().getName(), groupName)
                //开始执行时间
                .startAt(this.executorJobStartTime)
                //开始结束时间
                //.endAt(executorEndTime)
                //执行器
                .withSchedule(schedulerBuilder)
                .build();
        return this;
    }

    private JobDataMap jobDataInit() {
        JobDataMap jobDataMap = new JobDataMap();
        return jobDataMap;
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
        this.executorJobStartTime = DateTimeUtil.toDate(LocalDateTime.now().plusSeconds(2));
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
