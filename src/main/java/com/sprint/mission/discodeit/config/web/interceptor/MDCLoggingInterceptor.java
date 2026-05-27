package com.sprint.mission.discodeit.config.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
@Slf4j
// MVC Interceptor
public class MDCLoggingInterceptor implements HandlerInterceptor {

    private static final String MDC_REQUEST_ID = "requestId";
    private static final String MDC_REQUEST_METHOD = "requestMethod";
    private static final String MDC_REQUEST_URI = "requestUri";

    private static final String REQUEST_ID_HEADER = "Discodeit-Request-ID";
    private static final String START_TIME_ATTR = "startTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestId = UUID.randomUUID().toString();
        String requestMethod = request.getMethod();
        String requestUri  = request.getRequestURI();

        MDC.put(MDC_REQUEST_ID, requestId);
        MDC.put(MDC_REQUEST_METHOD, requestMethod);
        MDC.put(MDC_REQUEST_URI, requestUri);

        response.setHeader(REQUEST_ID_HEADER, requestId);
        request.setAttribute(START_TIME_ATTR, System.currentTimeMillis());

        log.debug("[HTTP_REQUEST] 요청 시작");
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        try {
            Long startTime = (Long) request.getAttribute(START_TIME_ATTR);
            long duration = startTime == null ? 0L : System.currentTimeMillis() - startTime;
            int status = response.getStatus();

            if (status >= 500) {
                log.error("[HTTP_RESPONSE] 요청 종료 | status={} | {}ms", status, duration);
            } else if (status >= 400) {
                log.warn("[HTTP_RESPONSE] 요청 종료 | status={} | {}ms", status, duration);
            } else {
                log.info("[HTTP_RESPONSE] 요청 종료 | status={} | {}ms", status, duration);
            }
        } finally {
            MDC.clear();
        }

        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
