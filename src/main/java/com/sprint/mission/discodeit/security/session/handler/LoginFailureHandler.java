package com.sprint.mission.discodeit.security.session.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

/*
    LoginFailureHandler
    -------------------
    로그인 실패 시, 프론트 엔드에게 401 Unauthorized 반환
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoginFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.error("[LOGIN_FAILED] 로그인 실패: {}", exception.getMessage());

        // JSON 형태로 401 Unauthorized과 함께 반환
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // 에러 응답 생성
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .code("AUTH_FAILURE")
                .message("Invalid username or password")
                .exceptionType(exception.getClass().getSimpleName())
                .details(null)
                .build();

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
