package com.sprint.mission.discodeit.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MDCLoggingFilter extends OncePerRequestFilter {

  private static final String REQUEST_ID = "request_id";
  private static final String REQUEST_METHOD = "request_method";
  private static final String REQUEST_URL = "request_url";
  private static final String HEADER_REQUEST_ID = "Discodeit-Request-ID";

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    // 랜덤 생성
    String requestId = UUID.randomUUID().toString().substring(0, 8);//8자리까지만

    // MDC에 정보 담기
    MDC.put(REQUEST_ID, requestId);
    MDC.put(REQUEST_METHOD, request.getMethod());
    MDC.put(REQUEST_URL, request.getRequestURI());

    // 응답 헤더에 포함
    response.setHeader(HEADER_REQUEST_ID, requestId);

    try {
      filterChain.doFilter(request, response);
    } finally {
      MDC.clear();
    }
  }

}
