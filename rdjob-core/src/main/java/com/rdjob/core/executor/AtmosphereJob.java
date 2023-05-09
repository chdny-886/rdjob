package com.rdjob.core.executor;

import com.rdjob.core.executor.scheduling.DefaultSchedulingExecutor;

import java.lang.annotation.*;

/**
 * @description:
 * @author: ChenDong
 * @time: 2022/4/19 14:39
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AtmosphereJob {

    /**
     * job名字
     * 需提前数据库ds_ap_job中添加
     */
    String jobName();

    /**
     * job分组
     * 需提前数据库ds_ap_job中添加
     */
    String jobGroup();

    /**
     * 执行器名称
     * 需提前数据库ds_ap_executor中添加
     */
    String executeName() default "default_executor";

    /**
     * 执行器类型,默认quartz,spring的方式暂时未实现
     */
    AtmosphereExecutorType executeType() default AtmosphereExecutorType.QUARTZ;

    /**
     * 执行器对象
     * 默认DefaultSchedulingExecutor,通过nextJobTime()刷新执行器时间的执行器
     * CronSchedulingExecutor,Cron表达式执行器
     * SpringSchedulingExecutor,spring task执行器，暂未实现
     */
    Class<? extends SchedulingExecutor> schedulingExecutor() default DefaultSchedulingExecutor.class;
}
