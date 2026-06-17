package com.sprint.mission.discodeit.config;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;
import java.util.concurrent.Executor;

/*
    AsyncConfig
    ------------
    비동기 처리를 위한 설정
 */
@Configuration
@EnableAsync
@EnableRetry
public class AsyncConfig {
    // 비동기 작업 처리를 위한 스레드 풀 빈 정의
    @Bean(name = "eventTaskExecutor")
    public Executor eventTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(10);                   // 기본 스레드 수
        executor.setMaxPoolSize(20);                    // 최대 스레드 수
        executor.setQueueCapacity(500);                 // 대기 큐 크기
        executor.setThreadNamePrefix("AsyncExec-");     // 스레드 접두사

        executor.setTaskDecorator(new MdcSecurityContextTaskDecorator());
        executor.initialize();
        return executor;
    }

    // 커스텀 데코레이터
    private static class MdcSecurityContextTaskDecorator implements TaskDecorator {
        @Override
        public Runnable decorate(Runnable runnable) {
            // 메인 스래드 내의 로그 추적용 MDC 및 Security 인증 정보 복사
            Map<String, String> contextMap = MDC.getCopyOfContextMap();
            SecurityContext securityContext = SecurityContextHolder.getContext();

            return () -> {
                try {
                    // 비동기 스레드 내 복사한 정보 주입
                    if (contextMap != null) {
                        MDC.setContextMap(contextMap);
                    }
                    SecurityContextHolder.setContext(securityContext);

                    // 비동기 작업 수행
                    runnable.run();
                } finally {
                    // 메모리 누수 방지
                    MDC.clear();
                    SecurityContextHolder.clearContext();
                }
            };
        }
    }
}
