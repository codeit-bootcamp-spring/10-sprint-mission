package com.sprint.mission.discodeit.auth.security.handler;

import com.sprint.mission.discodeit.auth.service.AuthService;
import com.sprint.mission.discodeit.global.security.jwt.CookieProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTLogoutHandler implements LogoutHandler {

  private final AuthService authService;
  private final CookieProvider cookieProvider;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {
    Cookie cookie = WebUtils.getCookie(request, cookieProvider.getRefreshTokenName());
    if (cookie != null) {
      authService.deleteRefreshToken(cookie.getValue());
    }
    ResponseCookie deleteCookie = cookieProvider.createDeleteCookie();
    response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
  }
}
