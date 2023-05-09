package com.rdjob.core.executor;

import org.quartz.JobKey;
import org.quartz.SchedulerException;

/**
 * @description:
 * @author: ChenDong
 * @time: 2022/4/14 17:21
 */
public interface SchedulingExecutor {

    /**
     * 按job配置周期运行
     *
     * @throws SchedulerException
     */
    void start() throws SchedulerException;

    /**
     * 立即运行
     *
     * @throws SchedulerException
     */
    default void run() throws SchedulerException {
    }

    /**
     * 终止
     *
     * @throws SchedulerException
     */
    default void stop() throws SchedulerException {
    }

    /**
     * 删除
     *
     * @throws SchedulerException
     */
    default void delete() throws SchedulerException {
    }

    /**
     * 暂停
     *
     * @throws SchedulerException
     */
    default void pause() throws SchedulerException {
    }

    /**
     * 恢复
     *
     * @throws SchedulerException
     */
    default void resume() throws SchedulerException {
    }

    default JobKey getJobKey() throws SchedulerException {
        return null;
    }
}
