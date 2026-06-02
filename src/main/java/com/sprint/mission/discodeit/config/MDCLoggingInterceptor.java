package com.sprint.mission.discodeit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class MDCLoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) {
        // 8자리 UUID 생성
        String requestId = UUID.randomUUID().toString().substring(0, 8);

        // 로그백 패턴에 정의한 키값과 동일하게 저장
        MDC.put("requestId", requestId);
        MDC.put("requestMethod", request.getMethod());
        MDC.put("requestUri", request.getRequestURI());

        // 응답 헤더 설정
        response.setHeader("Discodeit-Request-ID", requestId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex) {
        MDC.clear(); // 요청 종료 후 반드시 초기화
    }
}

