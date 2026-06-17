package com.sprint.mission.discodeit.decorator;

import org.springframework.core.task.TaskDecorator;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextTaskDecorator implements TaskDecorator {
    @Override
    public Runnable decorate(Runnable runnable) {
        // 제출 시점(요청 스레드)의 인증 정보를 캡처
        SecurityContext context = SecurityContextHolder.getContext();
        return () -> {
            try {
                SecurityContextHolder.setContext(context);
                runnable.run();
            } finally {
                // 스레드 풀 재사용 대비 정리
                SecurityContextHolder.clearContext();
            }
        };
    }
}
