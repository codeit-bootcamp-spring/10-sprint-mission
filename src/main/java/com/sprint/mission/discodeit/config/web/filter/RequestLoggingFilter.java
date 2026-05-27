package com.sprint.mission.discodeit.config.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Request/Response 로깅 공통 Filter
 * <br>
 * HTTP 요청마다 Request와 Response를 공통으로 로깅
 */
//@Component // Spring Bean 등록
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException
    {
        long startTime = System.currentTimeMillis();
        String requestUri = request.getRequestURI();
        String method = request.getMethod();

        log.debug("[HTTP_REQUEST] 요청 API 시작: {} {}", method, requestUri);

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = response.getStatus();

            if (status >= 500) {
                log.error("[HTTP_RESPONSE] 요청 API 종료: {} {} | status={} | {}ms", method, requestUri, status, duration);
            } else if (status >= 400) {
                log.warn("[HTTP_RESPONSE] 요청 API 종료: {} {} | status={} | {}ms", method, requestUri, status, duration);
            } else {
                log.info("[HTTP_RESPONSE] 요청 API 종료: {} {} | status={} | {}ms", method, requestUri, status, duration);
            }
        }
    }
}
