package com.sprint.mission.discodeit.config;

import java.util.Map;
import java.util.concurrent.Executor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@EnableAsync
public class AsyncConfig {
  @Bean
  public Executor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

    executor.setCorePoolSize(4);

    executor.setMaxPoolSize(5);

    executor.setQueueCapacity(100);

    executor.setThreadNamePrefix("Async-");

    executor.initialize();
    return executor;
  }

  @Bean
  public TaskDecorator contextCopyingTaskDecorator() {
    return runnable -> {
      // @Async 의 스레드의 MDC 값 복사
      Map<String, String> mdcContext = MDC.getCopyOfContextMap();
      // 요청 스레드의 인증 정보 복사
      SecurityContext securityContext = SecurityContextHolder.getContext();

      return () -> {
        try {
          if (mdcContext != null) {
            MDC.setContextMap(mdcContext);
          }

          SecurityContextHolder.setContext(securityContext);

          runnable.run();
        } finally {
          // 비동기 스레드는 재사용 -> 정리
          MDC.clear();
          SecurityContextHolder.clearContext();
        }
      };
    };
  }
}
