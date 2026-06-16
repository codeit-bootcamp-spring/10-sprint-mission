package com.sprint.mission.discodeit.auth.jwt;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

/**
 * 리프레시 토큰 쿠키를 생성하고 삭제하는 전용 컴포넌트입니다.
 * 보안 정책(HttpOnly, Secure, SameSite 등)을 한 곳에서 관리합니다.
 */
@Component
public class JwtCookieManager {

    private static final long REFRESH_TOKEN_MAX_AGE = 7 * 24 * 60 * 60; // 7일

    /**
     * 리프레시 토큰을 HttpOnly 쿠키로 추가합니다.
     */
    public void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(false) // 배포 시 true로 변경 (HTTPS 필요)
                .path("/")
                .maxAge(REFRESH_TOKEN_MAX_AGE)
                .sameSite("Lax") // CSRF 방어
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    /**
     * 클라이언트의 리프레시 토큰 쿠키를 삭제합니다. (로그아웃 시 사용)
     */
    public void deleteRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .path("/")
                .maxAge(0) // 즉시 만료
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
