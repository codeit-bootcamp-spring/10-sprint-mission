package com.sprint.mission.discodeit.config;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.TaskExecutor;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@EnableAsync
@EnableRetry
public class AsyncConfig implements AsyncConfigurer {

  @Bean(name = "notificationTaskExecutor")
  public TaskExecutor notificationTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(10);
    executor.setThreadNamePrefix("notification-");
    executor.setTaskDecorator(new CompositeTaskDecorator(
        Arrays.asList(new MDCTaskDecorator(), new SecurityContextTaskDecorator())));
    executor.initialize();
    return executor;
  }

  @Bean(name = "binaryContentTaskExecutor")
  public TaskExecutor binaryContentTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(10);
    executor.setThreadNamePrefix("binary-content-");
    executor.setTaskDecorator(new CompositeTaskDecorator(
        Arrays.asList(new MDCTaskDecorator(), new SecurityContextTaskDecorator())));
    executor.initialize();
    return executor;
  }

  public class MDCTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
      // 현재 스레드 정보 복제
      Map<String, String> contextMap = MDC.getCopyOfContextMap();

      return () -> {
        try {
          // 복제한 컨텍스트로 설정
          if (contextMap != null) {
            MDC.setContextMap(contextMap);
          }
          runnable.run();
        } finally {
          MDC.clear();
        }
      };
    }
  }

  public class SecurityContextTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
      SecurityContext securityContext = SecurityContextHolder.getContext();
      return () -> {
        try {
          SecurityContextHolder.setContext(securityContext);
          runnable.run();
        } finally {
          SecurityContextHolder.clearContext();
        }
      };
    }
  }

  @RequiredArgsConstructor
  public class CompositeTaskDecorator implements TaskDecorator {

    private final List<TaskDecorator> decorators;

    @Override
    public Runnable decorate(Runnable runnable) {
      Runnable result = runnable;
      // 뒤에서부터 거꾸로
      for (int i = decorators.size() - 1; i >= 0; i--) {
        result = decorators.get(i).decorate(result);
      }
      return result;
    }
  }

}
