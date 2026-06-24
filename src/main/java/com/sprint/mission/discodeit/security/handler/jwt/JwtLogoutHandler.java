package com.sprint.mission.discodeit.security.handler.jwt;

import com.sprint.mission.discodeit.event.UserOnlineStatusUpdateEvent;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.security.registry.JwtRegistry;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        ResponseCookie logoutRefreshTokenCookie = ResponseCookie
                .from("REFRESH_TOKEN", "")
                .httpOnly(true)
                .secure(false) // local용
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, logoutRefreshTokenCookie.toString());

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            // 로그아웃 시 해당 유저의 모든 JwtInformation 삭제
            Arrays.stream(cookies)
                    .filter(cookie -> "REFRESH_TOKEN".equals(cookie.getName()))
                    .findFirst()
                    .ifPresent(cookie -> {
                        String refreshToken = cookie.getValue();

                        if (jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
                            UUID userId = UUID.fromString(jwtTokenProvider.getSubject(refreshToken));
                            jwtRegistry.invalidateJwtInformationByUserId(userId);

                            changeEventPublish(userId);
                        }
                    });
        }

        log.info("[AUTH_LOGOUT_SUCCESS] 로그아웃 성공");
    }

    private void changeEventPublish(UUID userId) {
        applicationEventPublisher.publishEvent(
                new UserOnlineStatusUpdateEvent(
                        userId,
                        null
                )
        );
    }
}
