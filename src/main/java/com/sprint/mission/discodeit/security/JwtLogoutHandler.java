package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.registry.JwtRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtLogoutHandler implements LogoutHandler {

  private final JwtRegistry jwtRegistry;
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {
    if (request.getCookies() == null) {
      return;
    }

    Arrays.stream(request.getCookies())
        .filter(cookie -> cookie.getName().equals(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME))
        .findFirst()
        .ifPresent(cookie -> {
          String refreshToken = cookie.getValue();
          ResponseCookie expiredCookie = ResponseCookie.from(
                  JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, "")
              .httpOnly(true)
              .path("/")
              .maxAge(0)
              .build();
          response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());
          log.debug("[LOGOUT] 브라우저 쿠키 정상 삭제 완료");

          try {
            if (jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
              UUID userId = UUID.fromString(jwtTokenProvider.getSubject(refreshToken));
              jwtRegistry.invalidateJwtInformationByUserId(userId);
            }
          } catch (Exception e) {
            log.warn("[LOGOUT] 만료되거나 유효하지 않은 Refresh Token의 접근");
          }
        });
  }
}