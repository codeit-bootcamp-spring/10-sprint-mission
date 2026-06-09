package com.sprint.mission.discodeit.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.TaskExecutor;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;

@Slf4j
@Configuration
@EnableAsync
@EnableRetry
public class AsyncConfig {

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("async-");
        executor.setTaskDecorator(taskDecorator());
        executor.initialize();
        return executor;
    }

    @Bean
    public TaskDecorator taskDecorator() {
        return runnable -> {
            Map<String, String> mdcContext = MDC.getCopyOfContextMap();
            SecurityContext securityContext = SecurityContextHolder.getContext();

            return () -> {
                try {
                    // 비동기 스레드에 MDC, SecurityContext 적용
                    if (mdcContext != null) {
                        MDC.setContextMap(mdcContext);
                    }
                    SecurityContextHolder.setContext(securityContext);
                    runnable.run();
                } finally {
                    // 비동기 스레드 정리
                    MDC.clear();
                    SecurityContextHolder.clearContext();
                }
            };
        };
    }
}
