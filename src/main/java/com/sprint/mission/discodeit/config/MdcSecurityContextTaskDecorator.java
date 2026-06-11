package com.sprint.mission.discodeit.config;

import java.util.Map;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

// MDC의 Request ID, SecurityContext의 인증 정보가 비동기 스레드에서도 유지되도록 하는 클래스
public class MdcSecurityContextTaskDecorator implements TaskDecorator {

  @Override
  public Runnable decorate(Runnable runnable) {
    // 메인 스레드의 Context 복사
    Map<String, String> contextMap = MDC.getCopyOfContextMap();
    SecurityContext securityContext = SecurityContextHolder.getContext();

    return () -> {
      try {
        // 비동기 스레드에 Context 주입
        if (contextMap != null) {
          MDC.setContextMap(contextMap);
        }
        SecurityContextHolder.setContext(securityContext);

        // 실제 비동기 로직 실행
        runnable.run();
      } finally {
        // 스레드 풀의 스레드는 재사용되므로, 작업 완료 후 초기화해야 메모리 누수와 권한 오염을 막을 수 있음
        MDC.clear();
        SecurityContextHolder.clearContext();
      }
    };
  }
}
