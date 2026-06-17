package com.sprint.mission.discodeit.config;

import java.util.Map;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@EnableAsync
@EnableRetry
public class AsyncConfig {

  @Bean
  public TaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(4);
    executor.setMaxPoolSize(8);
    executor.setQueueCapacity(50);
    executor.setThreadNamePrefix("async-task-");
    executor.setTaskDecorator(new MdcAndSecurityContextCopyTaskDecorator());
    executor.initialize();
    return executor;
  }

  private static class MdcAndSecurityContextCopyTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
      Map<String, String> contextMap = MDC.getCopyOfContextMap();
      SecurityContext securityContext = SecurityContextHolder.getContext();

      return () -> {
        if (contextMap != null) {
          MDC.setContextMap(contextMap);
        }

        SecurityContext previousContext = SecurityContextHolder.getContext();
        SecurityContextHolder.setContext(securityContext);

        try {
          runnable.run();
        } finally {
          SecurityContextHolder.setContext(previousContext);
          MDC.clear();
        }
      };
    }
  }
}
