package com.sprint.mission.discodeit.global.config.async;

import java.util.concurrent.Executor;
import org.springframework.boot.task.ThreadPoolTaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@Configuration
class NotificationAsyncConfig {

  @Bean(name = "notificationWorker")
  public Executor notificationWorker(ThreadPoolTaskExecutorBuilder builder) {
    return builder
        .corePoolSize(10)
        .maxPoolSize(30)
        .queueCapacity(20)
        .threadNamePrefix("noti-Async-")
        .build();
  }
}
