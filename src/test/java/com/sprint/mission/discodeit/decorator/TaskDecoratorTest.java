package com.sprint.mission.discodeit.decorator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class TaskDecoratorTest {

    @AfterEach
    void clearContext() {
        MDC.clear();
        SecurityContextHolder.clearContext();
    }

    private void runOnWorkerThread(Runnable runnable) throws InterruptedException {
        Thread worker = new Thread(runnable);
        worker.start();
        worker.join();
    }

    @Test
    @DisplayName("MdcTaskDecorator는 제출 스레드의 MDC를 워커 스레드로 전파한다.")
    void mdcDecorator_propagatesMdcToWorkerThread() throws InterruptedException {
        // given
        MDC.put("requestId", "req-123");
        AtomicReference<String> seenInWorker = new AtomicReference<>();
        Runnable decorated = new MdcTaskDecorator()
                .decorate(() -> seenInWorker.set(MDC.get("requestId")));

        // when
        runOnWorkerThread(decorated);

        // then
        assertThat(seenInWorker.get()).isEqualTo("req-123");
    }

    @Test
    @DisplayName("SecurityContextTaskDecorator는 제출 스레드의 인증 정보를 워커 스레드로 전파한다.")
    void securityDecorator_propagatesAuthenticationToWorkerThread() throws InterruptedException {
        // given
        Authentication auth =
                new UsernamePasswordAuthenticationToken("alice", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
        AtomicReference<Authentication> seenInWorker = new AtomicReference<>();
        Runnable decorated = new SecurityContextTaskDecorator().decorate(
                () -> seenInWorker.set(SecurityContextHolder.getContext().getAuthentication()));

        // when
        runOnWorkerThread(decorated);

        // then
        assertThat(seenInWorker.get()).isEqualTo(auth);
    }

    @Test
    @DisplayName("CompositeTaskDecorator는 MDC와 인증 정보를 함께 전파한다.")
    void compositeDecorator_propagatesBothMdcAndAuthentication() throws InterruptedException {
        // given
        MDC.put("requestId", "req-456");
        Authentication auth =
                new UsernamePasswordAuthenticationToken("bob", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        AtomicReference<String> seenRequestId = new AtomicReference<>();
        AtomicReference<Authentication> seenAuth = new AtomicReference<>();
        CompositeTaskDecorator composite = new CompositeTaskDecorator(
                List.of(new MdcTaskDecorator(), new SecurityContextTaskDecorator()));
        Runnable decorated = composite.decorate(() -> {
            seenRequestId.set(MDC.get("requestId"));
            seenAuth.set(SecurityContextHolder.getContext().getAuthentication());
        });

        // when
        runOnWorkerThread(decorated);

        // then
        assertThat(seenRequestId.get()).isEqualTo("req-456");
        assertThat(seenAuth.get()).isEqualTo(auth);
    }
}
