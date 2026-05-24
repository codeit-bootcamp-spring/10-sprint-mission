package com.sprint.mission.discodeit.global.infra.storage.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
public class FileUploadAsyncConfig {

  @Bean(name = "fileUploadExecutor")
  public Executor fileUploadExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(10);
    executor.setQueueCapacity(20);
    executor.setMaxPoolSize(30);
    executor.setThreadNamePrefix("FileUploadExecutor-Async-");
    executor.initialize();
    return executor;
  }

  @Bean(name = "fileUploadWorker")
  public Executor fileUploadWorker() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(10);
    executor.setQueueCapacity(20);
    executor.setMaxPoolSize(30);
    executor.setThreadNamePrefix("FileUploadWorker-Async-");
    executor.initialize();
    return executor;
  }
}
