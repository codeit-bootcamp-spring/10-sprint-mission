package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.config.decorator.ContextCopyingDecorator;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

  private final CustomAsyncExceptionHandler customAsyncExceptionHandler;

  public AsyncConfig(CustomAsyncExceptionHandler customAsyncExceptionHandler) {
    this.customAsyncExceptionHandler = customAsyncExceptionHandler;
  }

  @Override
  public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    return customAsyncExceptionHandler;
  }

  // 1. I/O Bound 전용 스레드 풀 (DB 접근, 파일 업로드, 외부 API 호출 등)
  // 특징: 넉넉한 스레드 수
  @Bean(name = "ioTaskExecutor") // 이름 지정
  public ThreadPoolTaskExecutor ioTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(18);  // PC의 물리 스레드 수
    executor.setMaxPoolSize(50);
    executor.setQueueCapacity(100);
    executor.setThreadNamePrefix("IO-Async-"); // Prefix 구별
    executor.setTaskDecorator(new ContextCopyingDecorator());
    executor.initialize();
    return executor;
  }

  // 2. CPU Bound 전용 스레드 풀 (복잡한 연산, 암호화, 이미지 변환 등)
  // 특징: CPU 스레드 수에 딱 맞춘 스레드 수 (Context Switching 최소화)
  @Bean(name = "cpuTaskExecutor") // 이름 지정
  public ThreadPoolTaskExecutor cpuTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(18); // PC의 물리 스레드 수
    executor.setMaxPoolSize(18);
    executor.setQueueCapacity(50);
    executor.setThreadNamePrefix("CPU-Async-"); // Prefix 구별
    executor.setTaskDecorator(new ContextCopyingDecorator());
    executor.initialize();
    return executor;
  }
}