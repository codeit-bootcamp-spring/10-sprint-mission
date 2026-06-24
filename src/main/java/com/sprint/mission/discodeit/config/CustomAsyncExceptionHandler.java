package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.event.SystemEvents;
import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * @Async 메서드에서 발생한 예외를 전역적으로 처리하는 핸들러입니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

  private final ApplicationEventPublisher eventPublisher;

  @Override
  public void handleUncaughtException(Throwable ex, Method method, Object... params) {
    String className = method.getDeclaringClass().getSimpleName();
    String methodName = method.getName();
    String arguments = params.length > 0 
        ? java.util.Arrays.toString(params) 
        : "None";

    String detailedMethodInfo = String.format("%s.%s()", className, methodName);
    
    Throwable rootCause = ex;
    while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
      rootCause = rootCause.getCause();
    }
    
    String errorMessageWithArgs = String.format("%s\nRoot Cause: %s\nArgs: %s", ex.getMessage(), rootCause.getMessage(), arguments);

    String mdcRequestId = MDC.get("requestId");
    String requestId = mdcRequestId != null ? mdcRequestId : "N/A";

    log.error("[Async Exception] {} 실행 중 비동기 예외 발생 (RequestID: {}): \n{}", detailedMethodInfo, requestId, errorMessageWithArgs, ex);

    // 발생한 예외를 범용 비동기 에러 이벤트로 발행 (Kafka를 거쳐 관리자 알림으로 전송됨)
    eventPublisher.publishEvent(new SystemEvents.AsyncErrorAlarm(requestId, detailedMethodInfo, errorMessageWithArgs));
  }
}
