package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.config.decorator.CompositeDecorator;
import com.sprint.mission.discodeit.config.decorator.MdcTaskDecorator;
import com.sprint.mission.discodeit.config.decorator.SecurityContextDecorator;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

  @Bean(name = "asyncExecutor")
  public TaskExecutor asyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(3);        // 최소 스레드 개수
    executor.setMaxPoolSize(6);         // 최대 스레드 개수
    executor.setQueueCapacity(100);     // 대기 큐 용량 100
    executor.setKeepAliveSeconds(10);   // 유휴 스레드 10초
    executor.setThreadNamePrefix("AsyncExecutor-");
    executor.setTaskDecorator(new CompositeDecorator(List.of(
        new MdcTaskDecorator(), new SecurityContextDecorator()
    )));
    executor.initialize();
    return executor;
  }

  @Bean(name = "eventTaskExecutor")
  public TaskExecutor eventTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(3);        // 최소 스레드 개수
    executor.setMaxPoolSize(6);         // 최대 스레드 개수
    executor.setQueueCapacity(100);     // 대기 큐 용량 100
    executor.setKeepAliveSeconds(10);   // 유휴 스레드 10초
    executor.setThreadNamePrefix("EventTaskExecutor-");
    executor.setTaskDecorator(new CompositeDecorator(List.of(
        new MdcTaskDecorator(), new SecurityContextDecorator()
    )));
    executor.initialize();
    return executor;
  }
}
