package com.sprint.mission.discodeit.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoggingInterceptor implements HandlerInterceptor {

    private final LogContentExtractor extractor;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) {

        //log.info("[START] [{}] {}", request.getMethod(), request.getRequestURI());
        log.info("[START]");
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex) {
        // 1. 요청 바디 추출
        String requestBody = extractor.extractRequestBody(request);

        // 2. 응답 바디 추출
        String responseBody = extractor.extractResponseBody(response, request);

        // 3. 통합 로그 출력
        //log.info("[END] [{}] {} ({}) | Req: {} | Res: {}",
                //request.getMethod(),
                //request.getRequestURI(),
        log.info("[END] Status: {} | Req: {} | Res: {}",
                response.getStatus(),
                requestBody.isEmpty() ? "None" : requestBody,
                responseBody.isEmpty() ? "None" : responseBody);

        if (ex != null) {
            log.error("[ERROR] Exception occurred during request processing: ", ex);
        }
    }
}