package com.sprint.mission.discodeit.config.decorator;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;

public class ContextCopyingDecorator implements TaskDecorator {
  @Override
  public Runnable decorate(Runnable runnable) {

    // decorate는 작업이 Executor에 제출되는 시점에 호출됩니다.
    // 이때는 아직 요청을 처리하던 원래 스레드이므로, 현재 MDC 값을 복사해 둘 수 있습니다.
    Map<String, String> contextMap = MDC.getCopyOfContextMap();
    SecurityContext securityContext = SecurityContextHolder.getContext();

    return () -> {
      try {
        // 실제 비동기 작업이 시작되기 직전에, 복사해 둔 MDC 값을 작업 스레드에 설정합니다.
        if (contextMap != null) {
          MDC.setContextMap(contextMap);
        }
        SecurityContextHolder.setContext(securityContext);
        runnable.run();
      } finally {
        // 스레드 풀의 스레드는 재사용됩니다. 정리하지 않으면 다음 작업 로그에 이전 요청 정보가 섞일 수 있습니다.
        MDC.clear();
        SecurityContextHolder.clearContext();
      }
    };
  }
}