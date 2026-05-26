package com.sprint.mission.discodeit.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtLogoutHandler implements LogoutHandler {

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
          cookie.setValue("");
          cookie.setPath("/"); // 쿠키 생성 시 설정했던 Path와 일치해야 삭제
          cookie.setMaxAge(0); // 수명을 0으로 설정
          cookie.setHttpOnly(true);

          response.addCookie(cookie);
          log.info("Refresh token cookie deleted successfully.");
        });
  }
}
