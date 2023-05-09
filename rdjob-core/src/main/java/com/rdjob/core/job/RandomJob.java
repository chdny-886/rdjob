package com.rdjob.core.job;

import com.rdjob.core.executor.JobTimeWrapper;
import org.quartz.Job;

/**
 * @description:
 * @author: ChenDong
 * @time: 2022/4/14 18:28
 */
public interface RandomJob extends Job {

    /**
     * 下一次的任务执行时间（秒）
     */
    JobTimeWrapper nextJobTime();

    /**
     * cron表达式
     * @return
     */
    default String cronExpression() {
        return "";
    }

    /**
     * Job的名称,默认:jobName-group
     */
    String jobKey();

}
