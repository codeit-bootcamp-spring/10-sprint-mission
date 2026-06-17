package com.sprint.mission.discodeit.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;
import org.springframework.cache.annotation.CacheEvict;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    private final JwtRegistry jwtRegistry;
    private final JwtTokenProvider jwtTokenProvider;

    @CacheEvict(cacheNames = "users", allEntries = true)
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) {
        if (request.getCookies() != null) {
            Arrays.stream(request.getCookies())
                    .filter(cookie -> cookie.getName().equals(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME))
                    .findFirst()
                    .ifPresent(cookie -> {
                        String refreshToken = cookie.getValue();
                        if (jwtTokenProvider.validateToken(refreshToken)) {
                            String subject = (String) jwtTokenProvider.getClaims(refreshToken).get("sub");
                            try {
                                UUID userId = UUID.fromString(subject);
                                jwtRegistry.invalidateJwtInformationByUserId(userId);
                                log.debug("로그아웃 - 레지스트리 무효화 완료, userId: {}", userId);
                            } catch (IllegalArgumentException e) {
                                log.warn("로그아웃 처리 중 userId 파싱 실패: {}", subject);
                            }
                        }
                    });
        }

        Cookie expiredCookie = new Cookie(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, null);
        expiredCookie.setHttpOnly(true);
        expiredCookie.setPath("/");
        expiredCookie.setMaxAge(0);
        response.addCookie(expiredCookie);

        log.debug("리프레시 토큰 쿠키 삭제 완료");
    }
}
