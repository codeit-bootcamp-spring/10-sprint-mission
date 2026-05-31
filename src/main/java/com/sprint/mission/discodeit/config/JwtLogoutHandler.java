package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.registry.JwtRegistry;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

  private final JwtRegistry jwtRegistry;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {

    // JwtRegistry에서 토큰 무효화
    Arrays.stream(request.getCookies())
        .filter(cookie -> cookie.getName().equals(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME))
        .findFirst()
        .ifPresent(cookie -> jwtRegistry.removeJwtInformationByRefreshToken(cookie.getValue())
        );

    // 브라우저에게 null값인 Refresh Token 쿠키값을 전달하기 위해 생성
    Cookie cookie = new Cookie("REFRESH_TOKEN", null);

    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setMaxAge(0); // MaxAge를 0으로 두어 만료시킴

    response.addCookie(cookie); // 응답에 해당 만료된 쿠키를 포함해서 보냄

  }
}
