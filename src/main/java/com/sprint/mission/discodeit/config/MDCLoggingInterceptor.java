package com.sprint.mission.discodeit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

// 요청마다 MDC에 컨텍스트 정보를 추가하는 인터셉터
@Slf4j
public class MDCLoggingInterceptor implements HandlerInterceptor {

  // MDC 로깅에 사용되는 상수 정의
  public static final String REQUEST_ID = "request_id";
  public static final String REQUEST_METHOD = "request_method";
  public static final String REQUEST_URI = "request_uri";

  public static final String REQUEST_ID_HEADER = "Discodeit-Request-ID";

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
      Object handler) {
    // 요청 ID 생성
    String requestId = UUID.randomUUID().toString().substring(0, 8);

    // MDC에 정보 저장
    MDC.put(REQUEST_ID, requestId);
    MDC.put(REQUEST_METHOD, request.getMethod());
    MDC.put(REQUEST_URI, request.getRequestURI());

    // 응답 헤더에 Request ID 포함
    response.setHeader(REQUEST_ID_HEADER, requestId);

    log.debug("Request started");
    return true; // 요청을 컨트롤러로 계속 진행시킴
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) {
    // 요청 처리 후 MDC 데이터 정리
    log.debug("Request completed");
    MDC.clear();
  }
}
