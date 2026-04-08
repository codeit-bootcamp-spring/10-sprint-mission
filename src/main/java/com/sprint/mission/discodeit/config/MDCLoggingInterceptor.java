package com.sprint.mission.discodeit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
public class MDCLoggingInterceptor implements HandlerInterceptor {

    private static final String REQUEST_ID_HEADER = "Discodeit-Request-ID";
    private static final String REQUEST_ID_MDC_KEY = "requestId";
    private static final String REQUEST_METHOD_MDC_KEY = "requestMethod";
    private static final String REQUEST_URL_MDC_KEY = "requestUrl";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestId = UUID.randomUUID().toString();
        MDC.put(REQUEST_ID_MDC_KEY, requestId);
        MDC.put(REQUEST_METHOD_MDC_KEY, request.getMethod());
        MDC.put(REQUEST_URL_MDC_KEY, request.getRequestURI());

        response.addHeader(REQUEST_ID_HEADER, requestId);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        MDC.remove(REQUEST_ID_MDC_KEY);
        MDC.remove(REQUEST_METHOD_MDC_KEY);
        MDC.remove(REQUEST_URL_MDC_KEY);
    }
}
