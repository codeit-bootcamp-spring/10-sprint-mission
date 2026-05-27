package com.sprint.mission.discodeit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

public class MDCLoggingInterceptor implements HandlerInterceptor {

    /*
    - preHandle: 컨트롤러 실행 전 호출 -> MDC 세팅
    - afterCompletion: 응답이 완전 끝난 후 호출 -> MDC 해제
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("request_id", requestId);
        MDC.put("method", request.getMethod());
        MDC.put("uri", request.getRequestURI());
        response.setHeader("Discodeit-Request-ID", requestId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        MDC.clear();
    }
}
