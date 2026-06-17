package com.sprint.mission.discodeit.decorator;

import org.springframework.core.task.TaskDecorator;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

// 요청 스레드의 SecurityContext를 비동기 작업 스레드로 복사하는 Decorator
public class SecurityContextTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        // Decorate는 작업이 Executor에 제출되는 시점에 호출됨
        // 이때는 아직 요청을 처리하던 원래 스레드이므로, 현재 SecurityContext 값을 복사해 둘 수 있다.
        SecurityContext securityContext = SecurityContextHolder.getContext();

        return () -> {
            try {
                // 실제 비동기 작업이 실행되기 직전에
                // 복사해둔 SecurityContext 값을 직업 스레드에 설정
                if (securityContext != null) {
                    SecurityContextHolder.setContext(securityContext);
                    runnable.run();
                }
            } finally {
                // 스레드 풀의 스레드는 재사용되기 때문에
                // 정리하지 않으면 다음 작업 로그에 이전 요청 정보가 섞일 수 있음
                SecurityContextHolder.clearContext();
            }
        };
    }
}
