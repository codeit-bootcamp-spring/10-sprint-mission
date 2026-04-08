package com.sprint.mission.discodeit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
public class MDCLoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestId = UUID.randomUUID().toString().substring(0, 8);

        // MDC에 요청 정보 저장
        MDC.put("requestId", requestId);
        MDC.put("requestMethod", request.getMethod());
        MDC.put("requestUrl", request.getRequestURI());

        // 클라이언트가 추적할 수 있도록 응답 헤더에 Request ID 포함
        response.setHeader("Discodeit-Request-ID", requestId);

        return true;
    }

    @Override
    public void afterCompletion(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull Object handler, Exception ex) {
        // 스레드 풀 환경에서 메모리 누수 및 이전 데이터 잔류를 막기 위해 초기화
        MDC.clear();
    }
}