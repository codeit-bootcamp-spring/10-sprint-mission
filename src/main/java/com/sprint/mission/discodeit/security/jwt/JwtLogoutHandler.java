package com.sprint.mission.discodeit.security.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

    private final JwtRegistry jwtRegistry;

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            Arrays.stream(cookies)
                    .filter(cookie -> REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName()))
                    .findFirst()
                    .ifPresent(cookie ->
                            jwtRegistry.invalidateJwtInformationByRefreshToken(cookie.getValue())
                    );
        }

        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);
    }
}
