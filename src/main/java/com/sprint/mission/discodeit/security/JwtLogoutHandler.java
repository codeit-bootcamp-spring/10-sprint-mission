package com.sprint.mission.discodeit.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;
    private final CacheManager cacheManager;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        // 쿠키에서 리프레시 토큰으로 Registry 무효화
        if (request.getCookies() != null) {
            Arrays.stream(request.getCookies())
                    .filter(cookie -> cookie.getName().equals("REFRESH_TOKEN"))
                    .findFirst()
                    .ifPresent(cookie -> {
                        String refreshToken = cookie.getValue();
                        if(refreshToken != null && jwtTokenProvider.validate(refreshToken)) {
                            UUID userId = jwtTokenProvider.getUserId(cookie.getValue());
                            jwtRegistry.invalidateJwtInformationByUserId(userId);
                            log.debug("JWT 무효화 완료: userId={}", userId);
                        }
                    });
        }

        // REFRESH_TOKEN 쿠키 삭제
        Cookie refreshCookie = new Cookie("REFRESH_TOKEN", null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);  // 즉시 만료
        response.addCookie(refreshCookie);

        log.debug("리프레시 토큰 쿠키 삭제 완료");

        // 사용자 목록 캐시 무효화
        Cache cache = cacheManager.getCache("users");
        if (cache != null) {
            cache.clear();
        }
    }

}
