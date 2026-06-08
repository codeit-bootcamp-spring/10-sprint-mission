package com.sprint.mission.discodeit.config;

import java.util.Map;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Configuration
@EnableAsync
public class AsyncConfig {

  @Bean
  public TaskExecutor taskExecutor(TaskDecorator taskDecorator) {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(10);
    executor.setQueueCapacity(50);
    executor.setThreadNamePrefix("async-task-");
    executor.setTaskDecorator(taskDecorator);
    executor.initialize();
    return executor;
  }

  @Bean
  public TaskDecorator taskDecorator() {
    return runnable -> {
      RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
      SecurityContext securityContext = SecurityContextHolder.getContext();

      Map<String, String> mdcContext = MDC.getCopyOfContextMap();

      return () -> {
        try {
          if (requestAttributes != null) {
            RequestContextHolder.setRequestAttributes(requestAttributes);
          }
          SecurityContextHolder.setContext(securityContext);

          if (mdcContext != null) {
            MDC.setContextMap(mdcContext);
          }
          runnable.run();

        } finally {
          RequestContextHolder.resetRequestAttributes();
          SecurityContextHolder.clearContext();
          MDC.clear();
        }
      };
    };
  }
}
