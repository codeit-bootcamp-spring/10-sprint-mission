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

@Configuration
@EnableAsync
public class AsyncConfig {

  @Bean(name = "taskExecutor")
  public TaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

    //기본으로 유지할 비동기 작업 스레드 수
    executor.setCorePoolSize(4);
    //작업이 많이 몰렸을 때 최대로 늘릴 수 있는 스레드 수
    executor.setMaxPoolSize(8);
    //스레드가 모두 바쁠 때 대기시킬 작업 개수
    executor.setQueueCapacity(100);
    //비동기 스레드 이름 앞에 붙는 prefix
    executor.setThreadNamePrefix("async-");
    executor.setTaskDecorator(new MdcSecurityContextTaskDecorator());
    executor.initialize();

    return executor;
  }

  private static class MdcSecurityContextTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
      Map<String, String> contextMap = MDC.getCopyOfContextMap();
      SecurityContext securityContext = SecurityContextHolder.getContext();

      return () -> {
        try {
          if (contextMap != null) {
            MDC.setContextMap(contextMap);
          }

          SecurityContextHolder.setContext(securityContext);
          runnable.run();
        } finally {
          MDC.clear();
          SecurityContextHolder.clearContext();
        }
      };
    }
  }
}
