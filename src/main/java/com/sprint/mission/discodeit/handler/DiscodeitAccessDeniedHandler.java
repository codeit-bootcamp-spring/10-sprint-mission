package com.sprint.mission.discodeit.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class DiscodeitAccessDeniedHandler implements AccessDeniedHandler {
    public final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        log.warn("권한이 없는 사용자의 접근 시도: {}", request.getRequestURI());

        // http 설정
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // 예외 객체 생성
        ErrorResponse errorResponse = new ErrorResponse(
                Instant.now(),
                "AUTH_ACCESS_DENIED",
                "이 기능을 사용할 권한이 없습니다.",
                Collections.emptyMap(),
                accessDeniedException.getClass().getSimpleName(),
                HttpServletResponse.SC_FORBIDDEN
        );

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
