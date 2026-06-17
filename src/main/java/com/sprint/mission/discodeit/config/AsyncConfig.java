package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.decorator.CompositeTaskDecorator;
import com.sprint.mission.discodeit.decorator.MdcTaskDecorator;
import com.sprint.mission.discodeit.decorator.SecurityContextTaskDecorator;
import com.sprint.mission.discodeit.handler.CustomAsyncExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
@RequiredArgsConstructor
public class AsyncConfig implements AsyncConfigurer {

    private final CustomAsyncExceptionHandler exceptionHandler;

    @Bean(name = "cpuTaskExecutor")
    public TaskExecutor cpuTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(50);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("cpuExecutor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setTaskDecorator(new CompositeTaskDecorator(
                List.of(new MdcTaskDecorator(), new SecurityContextTaskDecorator())));
        return executor;
    }

    @Bean(name = "ioTaskExecutor")
    public TaskExecutor ioTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(100);
        executor.setKeepAliveSeconds(160);
        executor.setThreadNamePrefix("ioExecutor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setTaskDecorator(new CompositeTaskDecorator(
                List.of(new MdcTaskDecorator(), new SecurityContextTaskDecorator())));
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
        executor.setThreadNamePrefix("eventExecutor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setTaskDecorator(new CompositeTaskDecorator(
                List.of(new MdcTaskDecorator(), new SecurityContextTaskDecorator())));
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return exceptionHandler;
    }

    @Override
    public Executor getAsyncExecutor() {
        return cpuTaskExecutor();
    }
}
