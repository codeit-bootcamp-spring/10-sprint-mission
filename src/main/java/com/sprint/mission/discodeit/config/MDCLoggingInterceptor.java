package com.sprint.mission.discodeit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import org.jspecify.annotations.Nullable;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class MDCLoggingInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    String requestId = UUID.randomUUID().toString().substring(0, 8);  // 예시대로 8개

    MDC.put("requestId", requestId);
    MDC.put("requestMethod", request.getMethod());
    MDC.put("requestURI", request.getRequestURI());

    response.setHeader("Discodeit-Request-ID", requestId);
    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, @Nullable Exception ex) throws Exception {
    // 컨트롤러에서 예외가 발생해도 반드시 실행됨
    // 메모리 누수 방지를 위해 정리
    MDC.clear();
  }
}
