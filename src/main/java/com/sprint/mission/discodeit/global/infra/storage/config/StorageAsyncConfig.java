package com.sprint.mission.discodeit.global.infra.storage.config;

import java.util.concurrent.Executor;
import org.springframework.boot.task.ThreadPoolTaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@Configuration
public class StorageAsyncConfig {

  @Bean(name = "fileUploadExecutor")
  public Executor fileUploadExecutor(ThreadPoolTaskExecutorBuilder builder) {
    return builder
        .corePoolSize(10)
        .maxPoolSize(30)
        .queueCapacity(20)
        .threadNamePrefix("FileUploadExecutor-Async-")
        .build();
  }

  @Bean(name = "fileUploadWorker")
  public Executor fileUploadWorker(ThreadPoolTaskExecutorBuilder builder) {
    return builder
        .corePoolSize(10)
        .maxPoolSize(30)
        .queueCapacity(20)
        .threadNamePrefix("FileUploadWorker-Async-")
        .build();
  }
}
