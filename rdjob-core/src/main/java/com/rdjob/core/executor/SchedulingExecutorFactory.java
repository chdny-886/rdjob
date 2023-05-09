package com.rdjob.core.executor;

import com.rdjob.core.executor.scheduling.CronSchedulingExecutor;
import com.rdjob.core.executor.scheduling.DefaultSchedulingExecutor;
import com.rdjob.core.executor.scheduling.SpringSchedulingExecutor;
import com.rdjob.core.job.RandomJob;
import com.rdjob.core.util.DateUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @description:
 * @author: ChenDong
 * @time: 2022/4/14 17:31
 */
@Slf4j
@Component
public class SchedulingExecutorFactory {

    @Autowired
    private Scheduler scheduler;

    @SneakyThrows
    public SchedulingExecutor getSchedulingExecutor(String executorKey, JobTimeWrapper nextJobTime, RandomJob job) {

        if (StringUtils.isEmpty(executorKey)) {
            throw new IllegalArgumentException("executorKey is null");
        }

        AtmosphereJob annotation = job.getClass().getAnnotation(AtmosphereJob.class);
        Class<? extends SchedulingExecutor> executorClass = annotation.schedulingExecutor();
        if (executorClass.getName().equals(DefaultSchedulingExecutor.class.getName())) {
            log.info("SchedulingExecutor job: {}-{}, 下一次执行时间: {}", executorKey, job.getClass().getSimpleName(), DateUtil.formatDateTime(nextJobTime.getNextJobTime()));
            return DefaultSchedulingExecutor
                    .createDefaultSchedulingExecutor()
                    .scheduler(scheduler)
                    .randomSeconds(nextJobTime.getNextTimeInterval())
                    .executorJobStartTime(nextJobTime.getNextJobTime())
                    .groupName(executorKey)
                    .executorJob(job)
                    .build();
        } else if (executorClass.getName().equals(CronSchedulingExecutor.class.getName())) {
            return CronSchedulingExecutor
                    .createCronSchedulingExecutor()
                    .scheduler(scheduler)
                    .groupName(executorKey)
                    .cronExpression(job.cronExpression())
                    .executorJob(job)
                    .build();
        } else if (executorClass.getName().equals(SpringSchedulingExecutor.class.getName())) {
            return SpringSchedulingExecutor
                    .createSpringSchedulingExecutor()
                    .build();
        }

        throw new IllegalArgumentException("不支持的SchedulingExecutor");
    }

}
