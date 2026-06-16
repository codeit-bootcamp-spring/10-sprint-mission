package com.sprint.mission.discodeit.handler.jwt;

import com.sprint.mission.discodeit.auth.jwt.JwtInformation;
import com.sprint.mission.discodeit.auth.jwt.JwtRegistry;
import com.sprint.mission.discodeit.repository.RefreshTokenRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {
    private final JwtRegistry jwtRegistry;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (request.getCookies() == null) return;

        Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("REFRESH_TOKEN"))
                .findFirst()
                .ifPresent(cookie -> {
                    String refreshToken = cookie.getValue();

                    JwtInformation dummyInfo = new JwtInformation(null, "", "");
                    jwtRegistry.rotateJwtInformation(refreshToken, dummyInfo);

                    log.info("로그아웃 완료: 리프레시 토큰 무효화 처리됨.");
                });
    }
}
