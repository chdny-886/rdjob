package com.rdjob.core.executor.scheduling;

import com.rdjob.core.executor.SchedulingExecutor;
import org.quartz.JobKey;
import org.quartz.SchedulerException;

/**
 * @description:
 * @author: ChenDong
 * @time: 2022/4/19 20:45
 */
public class DistributionSchedulingExecutor implements SchedulingExecutor {
    @Override
    public void start() throws SchedulerException {
        throw new NoSuchMethodError("目前不支持");
    }

    @Override
    public void run() throws SchedulerException {
        throw new NoSuchMethodError("目前不支持");
    }

    @Override
    public void stop() throws SchedulerException {
        throw new NoSuchMethodError("目前不支持");
    }

    @Override
    public void delete() throws SchedulerException {
        throw new NoSuchMethodError("目前不支持");
    }

    @Override
    public void pause() throws SchedulerException {
        throw new NoSuchMethodError("目前不支持");
    }

    @Override
    public void resume() throws SchedulerException {
        throw new NoSuchMethodError("目前不支持");
    }

    @Override
    public JobKey getJobKey() throws SchedulerException {
        throw new NoSuchMethodError("目前不支持");
    }
}
