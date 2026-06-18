package com.sprint.mission.discodeit.config;


import java.util.Map;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;


@Configuration
@EnableAsync
public class AsyncConfig {

  @Bean(name = "eventTaskExecutor")
  public TaskExecutor registerExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(4); // 기본 유지 스레드 수 4
    executor.setMaxPoolSize(8); // 큐가 찼을 때 최대 스레드 수는 8
    executor.setQueueCapacity(100); // 대기 큐 용량은 100
    executor.setThreadNamePrefix("event-async-"); // 접두사

    // TaskDecorator를 활용해 MDC의 RequestId, SecurityContext 인정 정보가 비동기 스레드에서도 유지되도록 구현
    // Decorator를 통해 비동기 작업이 실행하기 전
    // 비동기 작업을 호출한 스레드의 MDC와 SecurityContext를 실행 스레드에 복사
    // 작업이 끝나면 정리
    executor.setTaskDecorator(runnable -> {
      // Map 형태의 MDC 컨텍스트를 현재 요청 스레드에서 가져온다.
      Map<String, String> mdcContext = MDC.getCopyOfContextMap();
      // 현재 요청 스레드의 SecurityContext를 Holder를 통해 가져온다.
      SecurityContext securityContext = SecurityContextHolder.getContext();

      return () -> {
        try {
          // 작업을 호출한 스레드에 MDC 컨텍스트가 존재하면 비동기 스레드의 MDC 컨텍스트를 해당 컨텍스트로 세팅
          if (mdcContext != null) {
            MDC.setContextMap(mdcContext);
          }

          // 작업을 호출한 스레드의 SecurityContext를 비동기 작업 스레드에 세팅
          SecurityContextHolder.setContext(securityContext);

          runnable.run(); // 작업 수행

        } finally { // 작업이 끝나면 MDC/SecurityContext 제거하여 오염을 막는다.
          MDC.clear();
          SecurityContextHolder.clearContext();
        }
      };
    });

    executor.initialize(); // 초기화
    return executor;
  }
}
