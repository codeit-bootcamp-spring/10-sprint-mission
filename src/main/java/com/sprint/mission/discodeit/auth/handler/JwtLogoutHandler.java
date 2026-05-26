package com.sprint.mission.discodeit.auth.handler;

import com.sprint.mission.discodeit.auth.jwt.JwtRegistry;
import com.sprint.mission.discodeit.auth.jwt.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class JwtLogoutHandler implements LogoutHandler {
    private final JwtRegistry jwtRegistry;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            Arrays.stream(cookies)
                    .filter(cookie -> "REFRESH_TOKEN".equals(cookie.getName()))
                    .findFirst()
                    .ifPresent(cookie -> invalidateJwtInformation(cookie.getValue()));
        }

        Cookie refreshTokenCookie = new Cookie("REFRESH_TOKEN", null);

        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0);

        response.addCookie(refreshTokenCookie);
    }

    private void invalidateJwtInformation(String refreshToken) {
        try {
            Map<String, Object> claims = jwtTokenProvider.getClaims(refreshToken);
            String userIdValue = String.valueOf(claims.get("sub"));
            UUID userId = UUID.fromString(userIdValue);

            jwtRegistry.invalidateJwtInformationUserId(userId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invalidate JWT information by refresh token", e);
        }
    }
}
