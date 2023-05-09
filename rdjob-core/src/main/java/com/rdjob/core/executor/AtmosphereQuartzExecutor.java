package com.rdjob.core.executor;

import ch.qos.logback.core.util.Loader;
import com.rdjob.core.job.RandomJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

/**
 * 随机任务执行入口
 * @description:
 * @author: ChenDong
 * @time: 2022/4/13 11:28
 */
@Component
public class AtmosphereQuartzExecutor implements ApplicationRunner {

    @Autowired
    private SchedulingExecutorFactory executorFactory;
    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private AutowireCapableBeanFactory capableBeanFactory;
    @Autowired
    private DefaultListableBeanFactory defaultListableBeanFactory;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ResourcePatternResolver resolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        MetadataReaderFactory metaReader = new CachingMetadataReaderFactory(resourceLoader);
        Resource[] resources = resolver.getResources("classpath*:com/rdjob/core/executor/job/**/*.class");

        for (Resource resource : resources) {
            MetadataReader reader = metaReader.getMetadataReader(resource);
            String className = reader.getClassMetadata().getClassName();
            Class<?> cla = Loader.loadClass(className);
            if (cla.isAnnotationPresent(AtmosphereJob.class)) {
                AtmosphereJob annotation = cla.getAnnotation(AtmosphereJob.class);
                //这里只查询quartz的job，其他的job交给spring
                if (annotation.executeType() == AtmosphereExecutorType.QUARTZ) {
                    RandomJob bean = (RandomJob) ReflectionUtils.accessibleConstructor(cla).newInstance();
                    defaultListableBeanFactory.registerSingleton(lowerFirstCase(bean.getClass().getSimpleName()), bean);
                    capableBeanFactory.autowireBean(bean);
                    executorFactory.getSchedulingExecutor(bean.jobKey(), bean.nextJobTime(), bean).start();
                }
            }
        }
    }

    private String lowerFirstCase(String str){
        char[] chars = str.toCharArray();
        //首字母小写方法，大写会变成小写，如果小写首字母会消失
        chars[0] +=32;
        return String.valueOf(chars);
    }
}
