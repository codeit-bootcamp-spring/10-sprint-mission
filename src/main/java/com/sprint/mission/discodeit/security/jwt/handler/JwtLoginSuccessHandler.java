package com.sprint.mission.discodeit.security.jwt.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.response.auth.JwtDto;
import com.sprint.mission.discodeit.mapper.AuthMapper;
import com.sprint.mission.discodeit.security.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.jwt.JwtInformation;
import com.sprint.mission.discodeit.security.jwt.provider.JwtTokenProvider;
import com.sprint.mission.discodeit.security.jwt.registry.JwtRegistry;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/*
    JwtLoginSuccessHandler
    ----------------------
    로그인 성공 시, 프론트엔드에게 200 OK 및 UserDto 전달
 */
@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;

    private final ObjectMapper objectMapper;
    private final AuthMapper authMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 로그인을 요청한 사용자의 인증 정보 조회
        DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();

        // 토큰 발급
        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        // 사용자의 로그인 정보 (JWT 데이터 객체) 등록
        JwtInformation jwtInformation = new JwtInformation(userDetails.getUserDto(), accessToken, refreshToken);
        jwtRegistry.registerJwtInformation(jwtInformation);

        // 리프레시 토큰을 저장할 쿠키 객체
        Cookie refreshTokenCookie = new Cookie("REFRESH_TOKEN", refreshToken);
        refreshTokenCookie.setHttpOnly(true);               // 자바스크립트 읽기 방지
        refreshTokenCookie.setPath("/");                    // 모든 경로에서 사용
        refreshTokenCookie.setMaxAge(60 * 60 * 24 * 14);    // 유효 기간 (14일)
        response.addCookie(refreshTokenCookie);             // 응답 헤더 내 포함

        // JWT 응답 객체 생성
        JwtDto jwtDto = authMapper.toJwtDto(accessToken, userDetails.getUserDto());

        // JSON 형태로 200 OK와 함께 반환
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(jwtDto));
    }
}
