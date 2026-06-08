package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.decorator.CompositeTaskDecorator;
import com.sprint.mission.discodeit.decorator.MdcTaskDecorator;
import com.sprint.mission.discodeit.decorator.SecurityContextTaskDecorator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Bean(name = "discodeitTaskExecutor")
    public TaskExecutor discodeitExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(100);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("discodeit-async-");

        executor.setTaskDecorator(new CompositeTaskDecorator(
                List.of(new MdcTaskDecorator(), new SecurityContextTaskDecorator())
        ));

        executor.initialize();

        return executor;
    }

    @Bean(name = "eventTaskExecutor")
    public TaskExecutor eventTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(100);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("event-async-");

        executor.setTaskDecorator(new CompositeTaskDecorator(
                List.of(new MdcTaskDecorator(), new SecurityContextTaskDecorator())
        ));

        executor.initialize();

        return executor;
    }

    @Override
    public Executor getAsyncExecutor() {
        return discodeitExecutor();
    }
}
