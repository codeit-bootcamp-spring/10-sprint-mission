package com.sprint.mission.discodeit.security.handler.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.config.jwt.JwtProperties;
import com.sprint.mission.discodeit.dto.auth.JwtDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.event.UserOnlineStatusUpdateEvent;
import com.sprint.mission.discodeit.security.jwt.JwtInformation;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.security.registry.JwtRegistry;
import com.sprint.mission.discodeit.security.userdetails.DiscodeitUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

// 로그인 인증 성공 후 실행되는 핸들러 클래스로, 인증이 성공하면 JWT 토큰을 발급함
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final JwtRegistry jwtRegistry;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication // 인증 성공 결과 객체
    ) throws IOException, ServletException
    {
        DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();

        UserDto userDto = userDetails.getUserDto();

        // Access Token과 Refresh Token 발급
        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        // online true로 업데이트
        UserDto refreshUserDto = new UserDto(
                userDto.id(),
                userDto.username(),
                userDto.email(),
                userDto.profile(),
                true,
                userDto.role()
        );

        JwtInformation jwtInformation = new JwtInformation(
                refreshUserDto,
                accessToken,
                refreshToken
        );
        jwtRegistry.registerJwtInformation(jwtInformation);

        // 사용자 로그인 상태 변경으로 이벤트 전송
        changeEventPublish(refreshUserDto);

        // Refresh Token을 Cookie에 저장
        ResponseCookie refreshTokenCookie = ResponseCookie
                .from("REFRESH_TOKEN", refreshToken)
                .httpOnly(true)
                .secure(false) // local용
                .path("/") // 쿠키가 전송될 URL 범위 설정("/" : 모든 경로에 쿠키 전송)
                .maxAge(jwtProperties.getRefreshTokenExpirationTime()) // 쿠키 만료 시간 설정
                .sameSite("Strict")
                .build();

        // JwtDto 객체 생성
        JwtDto jwtDto = new JwtDto(
                refreshUserDto,
                accessToken
        );

        // response
        // 응답 상태 코드 200
        response.setStatus(HttpStatus.OK.value());
        // 응답 body가 JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // 한글 깨짐 방지를 위해 UTF-8 인코딩 설정
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        // 응답 header에 Cookie 추가
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
        // JwtDto 객체 -> JSON으로 변환 후 응답 Body에 담음
        objectMapper.writeValue(response.getWriter(), jwtDto);

        log.info("[AUTH_LOGIN_SUCCESS] 로그인 성공: userId={}", refreshUserDto.id());
    }

    private void changeEventPublish(UserDto userDto) {
        applicationEventPublisher.publishEvent(
                new UserOnlineStatusUpdateEvent(
                        null,
                        userDto
                )
        );
    }
}
