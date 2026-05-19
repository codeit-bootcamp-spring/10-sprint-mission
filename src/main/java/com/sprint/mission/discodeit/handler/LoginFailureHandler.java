package com.sprint.mission.discodeit.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoginFailureHandler implements AuthenticationFailureHandler {
    private final ObjectMapper objectMapper;
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        // http 헤더 설정
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // 에러 메시지 결정
        String errorMessage = "로그인에 실패했습니다.";
        if (exception instanceof BadCredentialsException || exception instanceof UsernameNotFoundException) {
            errorMessage = "아이디 또는 비밀번호가 일치하지 않습니다.";
        } else {
            log.error("알 수 없는 로그인 실패 사유: ", exception);
        }

        // 에러 객체 생성
        ErrorResponse errorResponse = new ErrorResponse(
                Instant.now(),
                "AUTH_LOGIN_FAILED",
                errorMessage,
                Collections.emptyMap(),
                exception.getClass().getSimpleName(),
                HttpServletResponse.SC_UNAUTHORIZED
        );

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
