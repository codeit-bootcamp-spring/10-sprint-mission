package com.sprint.mission.discodeit.security;

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
@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

  private final JwtRegistry jwtRegistry;
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {

    Cookie[] cookies = request.getCookies();

    // 1. 요청에 쿠키가 없으면 조기 종료
    if (cookies == null) {
      return;
    }

    // 2. 리프레시 토큰 쿠키를 찾아 삭제
    Arrays.stream(cookies)
        .filter(cookie -> cookie.getName().equals(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME))
        .findFirst()
        .ifPresent(cookie -> {
          String refreshToken = cookie.getValue();

          // 쿠키에서 빼낸 리프레시 토큰의 Subject(userId)를 읽어서 Registry 정보 삭제
          try {
            if (jwtTokenProvider.validateToken(refreshToken)) {
              String subject = jwtTokenProvider.getSubjectFromToken(refreshToken);
              UUID userId = UUID.fromString(subject);
              jwtRegistry.invalidateJwtInformationByUserId(userId);
            }
          } catch (Exception e) {
            log.warn("Failed to parse token during logout (already expired or malformed)");
          }

          cookie.setValue("");
          cookie.setPath("/"); // 쿠키 생성 시 설정했던 Path와 일치해야 삭제
          cookie.setMaxAge(0); // 수명을 0으로 설정
          cookie.setHttpOnly(true);

          response.addCookie(cookie);
          log.info("Refresh token cookie deleted successfully.");
        });
  }
}
