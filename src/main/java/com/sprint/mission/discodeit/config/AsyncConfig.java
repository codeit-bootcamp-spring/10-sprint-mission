package com.sprint.mission.discodeit.config;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;


@Configuration
/// @Async 붙은 메서드 만나면 별도 Thread에서 실행해라.
@EnableAsync
@RequiredArgsConstructor
public class AsyncConfig {

    /// 애플리케이션의 상태 지표를 저장하는 저장소
    /// Thread 몇개 사용중인지
    /// Queue 몇개 쌓였는지
    /// 요청수?
    /// 메모리?
    private final MeterRegistry meterRegistry;

    /**(예시 상황)
     (1)요청
     GET api/messages
     Authorization: Bearer xxx
     X-Request-ID: req-123
     ==============================

     (2)요청 스레드 상태
     Thread: http-nio-8080-1

     MDC:
     requestId = req-123

     SecurityContext
     Authentication = userid: 7f3a...
     ==============================

     **/

    @Bean
    public TaskDecorator contextPropagatingTaskDecorator() {
        return runnable -> {
            /// 요청 스레드 정보를 비동기 작업에 들고간다.

            /// 비동기 실행 직전 값 복사: Thread-1 값 복사해서 비동시 실행되는 Thread-2에 저장.
            /// captureMdcContext = {"requestId" : "req-123"}
            Map<String, String> capturedMdcContext = MDC.getCopyOfContextMap();
            /// captureAutentication = 로그인 사용자 인증 객체
            Authentication capturedAuthentication =
                    SecurityContextHolder.getContext().getAuthentication();

            /// 비동기 스레드 기존값 백업
            return () -> {
                /// 비동기 스레드는 ThreadPool에서 재상용된다.
                /// 예를들어, cpuExecutor-1이 예전에 다른 작업을 했을 수 있다. 그래서 현재 스레드에 있던 값을 일단 백업.
                Map<String, String> previousMdcContext = MDC.getCopyOfContextMap();
                SecurityContext previousSecurityContext = SecurityContextHolder.getContext();

                /// 비동기 스레드에 MDC 복원
                /// Thread: cpuExcutor-1(Thread-2)
                /// MDC: requestId = req-123
                /// 비동기 안에서 로그 찍으면 [cpuExecutor-1] [requestId=req-123] 알림 전송 시작 -> 이런식으로 requsetId가 유지된다.
                try {
                    if (capturedMdcContext != null) {
                        MDC.setContextMap(capturedMdcContext);
                    } else {
                        MDC.clear();
                    }

                    /// 비동기 스레드에 인증 정보 복원
                    /// 비동기 스레드에 새 SecurityContext를 만들고, 요청 스레드에서 복사해온 인증정보를 넣는다.
                    /// Thread: cpuExcutor-1(Thread-2)
                    /// SecurityContext:
                    /// Authentication = 로그인 사용자 인증 객체
                    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                    securityContext.setAuthentication(capturedAuthentication);
                    SecurityContextHolder.setContext(securityContext);

                    /// 실제 비동기 작업실행: @Async 메서드 실행
                    runnable.run();

                    /// ThreadPool은 스레드를 버리지 않고 재사용하지 때문에 정리해야된다.
                    /// 첫번째 요청 reqeustId = req-123
                    /// 두번째 요청 requestId = req-999
                    /// 정리하지않으면 두번째 작업 로그에 첫번째 requestId가 섞일 수 있다.
                } finally {
                    if (previousMdcContext != null) {
                        MDC.setContextMap(previousMdcContext);
                    } else {
                        MDC.clear();
                    }

                    SecurityContextHolder.setContext(previousSecurityContext);
                }
            };
        };
    }

    /// 실무에서는 cpu연산이나, I/O작업에 따라 적절한 스레드 풀을 구성하도록 DATA분석을 통해 설정.
    /// 현재 스레드 갯수 설정이나 이런 부분은 임의로 설정.
    @Bean(name = "ioTaskExecutor")
    public TaskExecutor ioTaskExecutor(TaskDecorator contextPropagatingTaskDecorator) {
        /// Spring에서 제공하는 Thread Pool
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 기본 스레드 갯수
        executor.setCorePoolSize(4);

        // 동시에 실행 가능한 최대 스레드 갯수
        /// (ThreadPoolExcutor 동작순서)
        /// 작업 100개 들어왔을때,
        /// (1)Thread 4개 생성
        /// (2)Queue에 작업 5 ~ 54까지 : 50개 대기
        /// (3)Queue까지 곽차면 max 증가. -> Thread 5 ~ 8 생성
        /// (4)그래도 넘치면 거부정책 실행.
        executor.setMaxPoolSize(8);
        // 대기 큐 용량
        executor.setQueueCapacity(50);
        /// core 초과 Thread 유지 시간.
        /// 1 ~ 8Thread 시작했다가 60초동안 놀면 5 ~ 8 제거된다.
        executor.setKeepAliveSeconds(60);
        // 스레드 이름 접두어 설정
        executor.setThreadNamePrefix("cpuExecutor-");
        // 작업 거부 정책 추가 설정
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setTaskDecorator(contextPropagatingTaskDecorator);

        // executor 초기화
        executor.initialize();

        // Micrometer로 스레드 풀 매트릭 지표 등록
        ThreadPoolExecutor threadPool = executor.getThreadPoolExecutor();
        Gauge.builder("executor.active.count", threadPool, ThreadPoolExecutor::getActiveCount)
                .description("현재 활성 스레드 수")
                .register(meterRegistry);

        Gauge.builder("executor.queue.size", threadPool, e -> e.getQueue().size())
                .description("대기 중인 작업 수")
                .register(meterRegistry);

        Gauge.builder("executor.pool.size", threadPool, ThreadPoolExecutor::getPoolSize)
                .description("현재 스레드 풀 크기")
                .register(meterRegistry);

        return executor;
    }





}
