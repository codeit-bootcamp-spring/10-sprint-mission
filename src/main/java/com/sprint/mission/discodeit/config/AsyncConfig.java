package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.decorator.CompositeTaskDecorator;
import com.sprint.mission.discodeit.decorator.MdcTaskDecorator;
import com.sprint.mission.discodeit.decorator.SecurityContextTaskDecorator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;

@Configuration
@EnableAsync
@EnableRetry
public class AsyncConfig {

    @Bean(name = "ioTaskExecutor")
    public TaskExecutor ioTaskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(100);
        executor.setKeepAliveSeconds(160);
        executor.setThreadNamePrefix("ioExecutor-");
        executor.setTaskDecorator(new CompositeTaskDecorator(
                List.of(new SecurityContextTaskDecorator(), new MdcTaskDecorator())
        ));
        executor.initialize();
        return executor;
    }

}
