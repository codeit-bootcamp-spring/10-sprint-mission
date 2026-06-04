package com.sprint.mission.discodeit.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@Configuration
@EnableAsync
public class AsyncConfig {

  @Bean
  public TaskExecutor registerExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(4); // 기본 유지 스레드 수 4
    executor.setMaxPoolSize(8); // 큐가 찼을 때 최대 스레드 수는 8
    executor.setQueueCapacity(100); // 대기 큐 용량은 100
    executor.setThreadNamePrefix("event-async-"); // 접두사
    executor.initialize(); // 초기화

    return executor;
  }
}
