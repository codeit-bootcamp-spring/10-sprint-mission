package com.sprint.mission.discodeit.config.async;

import com.sprint.mission.discodeit.decorator.CompositeTaskDecorator;
import com.sprint.mission.discodeit.decorator.MdcTaskDecorator;
import com.sprint.mission.discodeit.decorator.SecurityContextTaskDecorator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;

// 애플리케이션 전역의 비동기 실행 환경을 설정하는 클래스
@Configuration
@RequiredArgsConstructor
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Bean(name = "eventTaskExecutor")
    public TaskExecutor eventTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 기본 스레드 개수
        executor.setCorePoolSize(4);
        // Queue가 가득 찼을 때 늘릴 최대 스레드 수
        executor.setMaxPoolSize(8);
        // 모든 core 스레드가 바쁠 때 새 작업이 잠시 대기하는 Queue 용량
        executor.setQueueCapacity(50);
        // core 개수를 초과해 만들어진 스레드가 놀고 있을 때 유지되는 시간(초)
        executor.setKeepAliveSeconds(60);
        // 어떤 Executor를 실행했는지 구분하기 위한 스레드 이름 접두어
        executor.setThreadNamePrefix("eventTaskExecutor-");
        // TaskDecorator를 설정해 MDC, SecurityContext,트랜잭션 리소스 같은 컨텍스트를 작업 실행 전/후에 복사/정리
        executor.setTaskDecorator(
                new CompositeTaskDecorator(
                        List.of(
                                new MdcTaskDecorator(),
                                new SecurityContextTaskDecorator()
                        )
                )
        );

        // 내부 ThreadPoolExecutor 초기화
        executor.initialize();

        return executor;
    }
}
