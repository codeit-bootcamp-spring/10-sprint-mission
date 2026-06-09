package com.sprint.mission.discodeit.security.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

  private static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";
  private final JwtRegistry jwtRegistry;
  private final CacheManager cacheManager;

  @Override
  public void logout(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication
  ) {
    if (request.getCookies() != null) {
      Arrays.stream(request.getCookies())
          .filter(cookie -> REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName()))
          .findFirst()
          .ifPresent(cookie ->
              jwtRegistry.invalidateJwtInformationByRefreshToken(cookie.getValue())
          );
    }

    evictUsersCache();

    Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, null);
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setMaxAge(0);

    response.addCookie(cookie);
  }

  private void evictUsersCache() {
    Cache cache = cacheManager.getCache("users");
    if (cache != null) {
      cache.clear();
    }
  }
}