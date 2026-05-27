package com.sprint.mission.discodeit.security.handler;

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
import java.nio.charset.StandardCharsets;

// 로그인 인증 실패 후 실행되는 핸들러 클래스
@Component
@Slf4j
@RequiredArgsConstructor
public class LoginFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException
    {
        log.warn("[LOGIN_FAILURE] 로그인 실패: method={}, uri={}, exceptionType={}",
                request.getMethod(), request.getRequestURI(), exception.getClass().getSimpleName());

        int status = HttpStatus.UNAUTHORIZED.value();

        // 예외 응답 생성
        ErrorResponse errorResponse = ErrorResponse.loginFailure(exception, status);

        // response
        // 응답 상태 코드 401
        response.setStatus(status);
        // 응답 body가 JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // 한글 깨짐 방지를 위해 UTF-8 인코딩 설정
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        // ErrorResponse 객체 -> JSON으로 변환 후 응답 Body에 담음
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
