package com.sprint.mission.discodeit.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

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
          .filter(cookie -> cookie.getName().equals(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME))
          .findFirst()
          .ifPresent(cookie -> jwtRegistry.invalidateJwtInformationByRefreshToken(cookie.getValue()));
    }

    ResponseCookie refreshTokenCookie = ResponseCookie.from(
            JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME,
            ""
        )
        .httpOnly(true)
        .secure(false)
        .path("/")
        .sameSite("Strict")
        .maxAge(0)
        .build();

    Cache usersCache = cacheManager.getCache("users");
    if (usersCache != null) {
      usersCache.clear();
    }

    response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
  }
}
