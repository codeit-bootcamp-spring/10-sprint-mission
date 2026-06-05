package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.decorator.CompositeTaskDecorator;
import com.sprint.mission.discodeit.decorator.MdcTaskDecorator;
import com.sprint.mission.discodeit.decorator.SecurityContextTaskDecorator;
import java.util.List;
import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

  @Bean(name = "eventTaskExecutor")
  public Executor eventTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(10);
    executor.setQueueCapacity(100);
    executor.setThreadNamePrefix("Event-");
    executor.setTaskDecorator(new CompositeTaskDecorator(
        List.of(new MdcTaskDecorator(), new SecurityContextTaskDecorator())));
    executor.initialize();
    return executor;
  }
}
