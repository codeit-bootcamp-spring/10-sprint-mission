package com.sprint.mission.discodeit.config;

import java.util.Map;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "asyncTaskExecutor")
    public TaskExecutor asyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("discodeit-async-");
        executor.setTaskDecorator(contextCopyingTaskDecorator());
        executor.initialize();
        return executor;
    }

    private TaskDecorator contextCopyingTaskDecorator() {
        return task -> {
            Map<String, String> mdcContext = MDC.getCopyOfContextMap();
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            return () -> {
                Map<String, String> previousMdcContext = MDC.getCopyOfContextMap();
                SecurityContext previousSecurityContext = SecurityContextHolder.getContext();

                try {
                    if (mdcContext != null) {
                        MDC.setContextMap(mdcContext);
                    } else {
                        MDC.clear();
                    }

                    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                    securityContext.setAuthentication(authentication);
                    SecurityContextHolder.setContext(securityContext);

                    task.run();
                } finally {
                    MDC.clear();
                    if (previousMdcContext != null) {
                        MDC.setContextMap(previousMdcContext);
                    }

                    SecurityContextHolder.setContext(previousSecurityContext);
                }
            };
        };
    }
}