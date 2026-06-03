package com.sprint.mission.discodeit.security.jwt.handler;

import com.sprint.mission.discodeit.security.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.jwt.registry.JwtRegistry;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/*
    JwtLogoutHandler
    ----------------
    로그 아웃 시, 리프레시 토큰 무효화 처리
 */
@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    private final JwtRegistry jwtRegistry;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (request.getCookies() != null && authentication != null) {
            // 로그아웃을 요청한 사용자 인증 정보 조회
            DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();

            Arrays.stream(request.getCookies())
                    // 특정 사용자의 리프레시 토큰과 관련된 쿠키 필터링
                    .filter(cookie -> cookie.getName().equals("REFRESH_TOKEN"))
                    .findFirst()
                    .ifPresent(cookie -> {
                        jwtRegistry.invalidateJwtInformationByUserId(userDetails.getUserDto().id());
                    });
        }

        // 리프레시 토큰을 저장할 쿠키 객체
        Cookie refreshTokenCookie = new Cookie("REFRESH_TOKEN", null);
        refreshTokenCookie.setHttpOnly(true);               // 자바스크립트 읽기 방지
        refreshTokenCookie.setPath("/");                    // 모든 경로에서 사용
        refreshTokenCookie.setMaxAge(0);                    // 유효 기간 (0일)
        response.addCookie(refreshTokenCookie);             // 응답 헤더 내 포함
    }
}