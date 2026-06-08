package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.config.CacheNames;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtLogoutHandler implements LogoutHandler {

  private final JwtTokenProvider tokenProvider;
  private final JwtRegistry jwtRegistry;
  private final CacheManager cacheManager;

  @Autowired
  public JwtLogoutHandler(JwtTokenProvider tokenProvider, JwtRegistry jwtRegistry,
      CacheManager cacheManager) {
    this.tokenProvider = tokenProvider;
    this.jwtRegistry = jwtRegistry;
    this.cacheManager = cacheManager;
  }

  public JwtLogoutHandler(JwtTokenProvider tokenProvider, JwtRegistry jwtRegistry) {
    this(tokenProvider, jwtRegistry, null);
  }

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {

    // Clear refresh token cookie
    Cookie refreshTokenExpirationCookie = tokenProvider.genereateRefreshTokenExpirationCookie();
    response.addCookie(refreshTokenExpirationCookie);

    Arrays.stream(request.getCookies())
        .filter(cookie -> cookie.getName().equals(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME))
        .findFirst()
        .ifPresent(cookie -> {
          String refreshToken = cookie.getValue();
          UUID userId = tokenProvider.getUserId(refreshToken);
          jwtRegistry.invalidateJwtInformationByUserId(userId);
        });
    evictUsersCache();

    log.debug("JWT logout handler executed - refresh token cookie cleared");
  }

  private void evictUsersCache() {
    if (cacheManager == null) {
      return;
    }
    Cache cache = cacheManager.getCache(CacheNames.USERS);
    if (cache != null) {
      cache.clear();
    }
  }
}
