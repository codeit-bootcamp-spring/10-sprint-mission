package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.JwtRegistry;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    private final JwtRegistry jwtRegistry;
    private final CacheManager cacheManager;

    @Value("${discodeit.security.cookie.secure:false}")
    private boolean cookieSecure;

    @Override
    public void logout(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {
        // 비인증 상태에서도 쿠키 정리는 수행하도록 함
        if (authentication == null) {
            log.debug("비인증 상태의 로그아웃 요청 — 쿠키 정리만 수행");
        } else {
            log.info("로그아웃: principal={}", authentication.getName());
            if (authentication.getPrincipal() instanceof DiscodeitUserDetails ud) {
                jwtRegistry.invalidateJwtInformationByUserId(ud.getUserDto().id());
                // 로그아웃으로 online 상태가 바뀌므로 사용자 목록 캐시 무효화
                var usersCache = cacheManager.getCache("users");
                if (usersCache != null) {
                    usersCache.clear();
                }
            }
        }

        // 요청 쿠키에서 REFRESH_TOKEN 존재 여부 확인
        Cookie[] cookies = request.getCookies();
        boolean hasRefreshCookie = false;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                    hasRefreshCookie = true;
                    break;
                }
            }
        }
        if (!hasRefreshCookie) {
            return;
        }

        // 동일한 속성(Path, HttpOnly, SameSite)으로 maxAge=0 쿠키를 내려 브라우저에서 삭제
        ResponseCookie expired = ResponseCookie.from(
                        JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .sameSite("Lax")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, expired.toString());
    }
}
