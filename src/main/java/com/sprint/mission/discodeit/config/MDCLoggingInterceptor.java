package com.sprint.mission.discodeit.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;

@Slf4j
@Component
public class MDCLoggingInterceptor implements HandlerInterceptor {

    public static final String REQUEST_ID_HEADER = "Discodeit-Request-ID";
    private static final String REQUEST_ID_KEY = "requestId";
    private static final String REQUEST_METHOD_KEY = "requestMethod";
    private static final String REQUEST_URL_KEY = "requestUrl";

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {
        String requestId = UUID.randomUUID().toString();
        String requestUrl = buildRequestUrl(request);

        MDC.put(REQUEST_ID_KEY, requestId);
        MDC.put(REQUEST_METHOD_KEY, request.getMethod());
        MDC.put(REQUEST_URL_KEY, requestUrl);
        response.setHeader(REQUEST_ID_HEADER, requestId);

        log.debug("MDC logging context initialized");
        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex
    ) {
        MDC.remove(REQUEST_ID_KEY);
        MDC.remove(REQUEST_METHOD_KEY);
        MDC.remove(REQUEST_URL_KEY);
    }

    private String buildRequestUrl(HttpServletRequest request) {
        String requestUrl = request.getRequestURI();
        String queryString = request.getQueryString();

        if (queryString == null || queryString.isBlank()) {
            return requestUrl;
        }

        return requestUrl + "?" + queryString;
    }
}
