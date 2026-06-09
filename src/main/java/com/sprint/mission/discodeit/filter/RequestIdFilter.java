package com.sprint.mission.discodeit.filter;


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
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestIdFilter extends OncePerRequestFilter {

  public static final String REQUEST_ID_KEY = "requestId";
  public static final String REQUEST_ID_HEADER = "X-Request-ID";

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain
  ) throws ServletException, IOException {
    // 헤더 확인
    String requestId = request.getHeader(REQUEST_ID_HEADER);
    // 값이 있으면 만들기
    if (!StringUtils.hasText(requestId)) {
      requestId = UUID.randomUUID().toString().replace("-", "");
    }

    try {
      // MDC에 저장 - MCD.get(requestId) 가능
      MDC.put(REQUEST_ID_KEY, requestId);
      // 응답 헤더에 내려주기
      response.setHeader(REQUEST_ID_HEADER, requestId);

      filterChain.doFilter(request, response);
    } finally {
      // 요청 만료시 제거
      MDC.remove(REQUEST_ID_KEY);
    }
  }
}
