package com.rdjob.core.executor.scheduling;

import com.rdjob.core.executor.SchedulingExecutor;
import org.quartz.JobKey;
import org.quartz.SchedulerException;

/**
 * @description:
 * @author: ChenDong
 * @time: 2022/4/21 18:05
 */
public class SpringSchedulingExecutor implements SchedulingExecutor {

    public static SpringSchedulingExecutor createSpringSchedulingExecutor() {
        return new SpringSchedulingExecutor();
    }

    public SpringSchedulingExecutor build() {
        return this;
    }

    @Override
    public void start() throws SchedulerException {
        throw new SchedulerException("暂不支持SpringSchedulingExecutor");
    }

    @Override
    public void run() throws SchedulerException {
        throw new SchedulerException("暂不支持SpringSchedulingExecutor");
    }

    @Override
    public void stop() throws SchedulerException {
        throw new SchedulerException("暂不支持SpringSchedulingExecutor");
    }

    @Override
    public void delete() throws SchedulerException {
        throw new SchedulerException("暂不支持SpringSchedulingExecutor");
    }

    @Override
    public void pause() throws SchedulerException {
        throw new SchedulerException("暂不支持SpringSchedulingExecutor");
    }

    @Override
    public void resume() throws SchedulerException {
        throw new SchedulerException("暂不支持SpringSchedulingExecutor");
    }

    @Override
    public JobKey getJobKey() throws SchedulerException {
        throw new SchedulerException("暂不支持SpringSchedulingExecutor");
    }
}
