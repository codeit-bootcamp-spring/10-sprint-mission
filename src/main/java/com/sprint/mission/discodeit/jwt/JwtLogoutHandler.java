package com.sprint.mission.discodeit.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtLogoutHandler implements LogoutHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Arrays.stream(cookies)
                .filter(cookie -> JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName()))
                .findFirst()
                .ifPresent(cookie -> {
                    String refreshToken = cookie.getValue();
                    if (jwtTokenProvider.validateRefreshToken(refreshToken)) {
                        UUID userId = jwtTokenProvider.getUserId(refreshToken);
                        jwtRegistry.invalidateJwtInformationByUserId(userId);
                        log.info("로그아웃: userId={} JWT 무효화", userId);
                    }
                });
        }

        Cookie deleteCookie = new Cookie(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, null);
        deleteCookie.setHttpOnly(true);
        deleteCookie.setPath("/");
        deleteCookie.setMaxAge(0);
        response.addCookie(deleteCookie);

        log.info("로그아웃: REFRESH_TOKEN 쿠키 삭제");
    }
}
