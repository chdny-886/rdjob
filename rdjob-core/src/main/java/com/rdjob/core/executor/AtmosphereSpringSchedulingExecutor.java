package com.rdjob.core.executor;

import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * @description:
 * @author: ChenDong
 * @time: 2022/4/21 15:57
 */
//@Configuration
public class AtmosphereSpringSchedulingExecutor implements SchedulingConfigurer {

    //private InternationalDistributorGrowthJob internationalDistributorGrowthJob;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        //taskRegistrar.addTriggerTask(
        //        //1.添加任务内容(Runnable)
        //        () -> {
        //            System.out.println("执行动态定时任务: " + LocalDateTime.now().toLocalTime());
        //        },
        //        //2.设置执行周期(Trigger)
        //        triggerContext -> {
        //            return new PeriodicTrigger(1000).nextExecutionTime(triggerContext);
        //        }
        //);

        //taskRegistrar.addTriggerTask(internationalDistributorGrowthJob, new PeriodicTrigger(1000));
    }
}
