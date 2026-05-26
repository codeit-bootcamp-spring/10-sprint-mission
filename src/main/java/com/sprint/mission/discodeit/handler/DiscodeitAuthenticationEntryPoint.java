package com.sprint.mission.discodeit.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class DiscodeitAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.warn("로그인 하지 않은 사용자의 접근 시도: {}", request.getRequestURI());

        // http 설정
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // 예외 객체 생성
        ErrorResponse errorResponse = new ErrorResponse(
                Instant.now(),
                "AUTH_AUTH_UNAUTHORIZED",
                "로그인이 필요한 서비스 입니다.",
                Collections.emptyMap(),
                authException.getClass().getSimpleName(),
                HttpServletResponse.SC_UNAUTHORIZED
        );

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
