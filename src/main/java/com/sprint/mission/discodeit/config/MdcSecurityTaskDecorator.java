package com.sprint.mission.discodeit.config;

import java.util.Map;
import lombok.NonNull;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/*
비동기 스레드에서 요청 스레드의 MDC와 SecurityContext를 유지시키는 TaskDecorator 구현체
문제 상황: @Async 메서드는 스레드 풀의 별도 스레드에서 실행되므로,
요층 스레드의 ThreadLocal 기반 컨텍스트(MDC, SecurityContext)가 전달되지 않는다.
해결 방식: TaskDecorator를 통해 Runnable을 감싸서,
- 작업 제출 시점(요청 스레드): 컨텍스트를 캡처
- 작업 실행 시점(비동기 스레드): 캡처한 컨텍스트를 복원 후 실행
- 실행 완료 후: 스레드 풀 재사용을 고려해 컨텍스트를 정리
 */
public class MdcSecurityTaskDecorator implements TaskDecorator {

  // runnable: 비동기로 실행될 원본 작업
  // Runnable: MDC와 SecurityContext가 복원된 환경에서 실행되는 새로운 Runnable
  @Override
  @NonNull
  public Runnable decorate(@NonNull Runnable runnable) {
    // 1) 작업을 제출하는 시점 -> 요청 스레드의 컨텍스트를 캡처
    Map<String, String> contextMap = MDC.getCopyOfContextMap();
    SecurityContext securityContext = SecurityContextHolder.getContext();

    // 2) 실제로 실행되는 시점 -> 비동기 스레드 / 캡처한 컨텍스트를 복원
    // 람다는 스레드 풀의 비동기 스레드가 실행을 시작할 때 호출된다.
    return () -> {
      // 비동기 스레드의 현재 컨택스트를 백업
      Map<String, String> previousContext = MDC.getCopyOfContextMap();
      SecurityContext previousContextHolder = SecurityContextHolder.getContext();
      try {
        if (contextMap != null) {
          // 비동기 스레드의 MDC에 값 덮어씌우기
          MDC.setContextMap(contextMap);
        } else {
          // 요청 스레드의 MDC가 비어있었다면, 비동기 스레드에 잔류한 이전 작업의 MDC 값이 오염되지 않도록 초기화
          MDC.clear();
        }
        if (securityContext != null) {
          SecurityContextHolder.setContext(securityContext);
        } else {
          SecurityContextHolder.clearContext();
        }
        // 실제 비즈니스 로직을 실행
        runnable.run();
      } finally {
        // 실행 성공/실패 무관하게 반드시 수행
        // 3) 스레드 풀 재사용 시 다음 작업에 컨텍스트가 누수되지 않도록 정리
        if (previousContext != null) {
          // 스레드가 원래 가지고 있던 MDC로 복구
          MDC.setContextMap(previousContext);
        } else {
          MDC.clear();
        }
        // 무조건 초기화
        // 스레드에 인증 정보가 잔류하면 다음 작업이 이전 사용자의 권한으로 실행될 수 있는 보안 취약점 발생 방지
        SecurityContextHolder.clearContext();
      }
    };
  }
}
