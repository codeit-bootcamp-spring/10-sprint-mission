package com.sprint.mission.discodeit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

  @Bean(name = "eventTaskExecutor")
  public TaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(10); // 기본 대기 스레드 수
    executor.setMaxPoolSize(50);  // 최대 확장 스레드 수
    executor.setQueueCapacity(100); // 스레드가 꽉 찼을 때 대기할 큐 사이즈
    executor.setThreadNamePrefix("AsyncEvent-"); // 로그 추적용 접두사

    executor.setTaskDecorator(new MDCSecurityContextTaskDecorator());
    executor.initialize();

    return executor;
  }
}
