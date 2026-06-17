package com.sprint.mission.discodeit.config;

import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

  @Bean(name = "eventTaskExecutor")
  public TaskExecutor eventListenerExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5); // 기본 스레드 개수. 작업이 들어오면 우선 corePoolSize만큼 스레드를 유지
    executor.setMaxPoolSize(10); // 큐가 가득 찼을 때 최대 maxPoolSize까지 스레드를 늘려 동시에 처리
    executor.setQueueCapacity(100); // 모든 core 스레드가 바쁠 때 새 작업이 잠시 대기하는 큐의 용량
    executor.setThreadNamePrefix("event-"); // 로그에서 어떤 Executor가 실행했는지 구분하기 쉽도록 스레드 이름 접두어 설정
    executor.setWaitForTasksToCompleteOnShutdown(true); // shutdown 시 진행 중 작업 완료 대기
    executor.setAwaitTerminationSeconds(30); // 최대 30초 대기
    // 큐가 꽉 찼을 때 작업을 버리지 않고 호출한 스레드가 직접 실행하도록 하는 옵션
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    // MDC + SecurityContext 전파 데코레이터 등록 (initailize() 호출 전에)
    executor.setTaskDecorator(new MdcSecurityTaskDecorator());
    executor.initialize();
    return executor;
  }
}
