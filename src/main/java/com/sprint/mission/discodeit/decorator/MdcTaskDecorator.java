package com.sprint.mission.discodeit.decorator;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

import java.util.Map;

public class MdcTaskDecorator implements TaskDecorator {
    @Override
    public Runnable decorate(Runnable runnable) {
        // 현재 스레드 로컬에 저장된 로깅 관련 컨텍스트를 가져옴
        Map<String, String> contextMap = MDC.getCopyOfContextMap();

        // 새로운 스레드를 사용하는 부분은 여기부터임
        return () -> {
            try {
                // 비동기 스레드에 기존 MDC 컨텍스트 복원
                if (contextMap != null) {
                    MDC.setContextMap(contextMap);
                }
                runnable.run();
            } finally {
                // 수행한 후
                MDC.clear();
            }
        };
    }
}
