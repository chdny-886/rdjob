package com.rdjob.core.config;

import com.rdjob.core.executor.AtmosphereJobFactory;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * @description:
 * @author: ChenDong
 * @time: 2022/4/13 17:33
 */
@Configuration
public class AtmosphereConfiguration {
    @Autowired
    private AtmosphereJobFactory atmosphereJobFactory;

    @Bean
    public Scheduler scheduler() {
        return schedulerFactoryBean().getScheduler();
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setJobFactory(atmosphereJobFactory);
        return schedulerFactoryBean;
    }

}
