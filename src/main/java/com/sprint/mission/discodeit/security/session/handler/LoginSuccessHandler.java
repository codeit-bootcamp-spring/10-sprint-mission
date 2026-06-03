package com.sprint.mission.discodeit.security.session.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.security.auth.DiscodeitUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/*
    LoginSuccessHandler
    -------------------
    로그인 성공 시, 프론트엔드에게 200 OK 및 UserDto 전달
 */
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;        // 자바 객체 -> JSON 변환

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 인증이 완료된 사용자의 정보 조회
        DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();

        // JSON 형태로 200 OK와 함께 반환
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(response.getWriter(), userDetails.getUserDto());
    }
}
