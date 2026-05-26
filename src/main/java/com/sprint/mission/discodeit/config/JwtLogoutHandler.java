package com.sprint.mission.discodeit.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
public class JwtLogoutHandler implements LogoutHandler {

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {
    // 브라우저에게 null값인 Refresh Token 쿠키값을 전달하기 위해 생성
    Cookie cookie = new Cookie("REFRESH_TOKEN", null);

    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setMaxAge(0); // MaxAge를 0으로 두어 만료시킴

    response.addCookie(cookie); // 응답에 해당 만료된 쿠키를 포함해서 보냄
  }
}
