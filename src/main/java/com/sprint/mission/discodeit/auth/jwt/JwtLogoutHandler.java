package com.sprint.mission.discodeit.auth.jwt;

import com.sprint.mission.discodeit.config.CacheConfig.CacheNames;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;
  private final CacheManager cacheManager;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {
    ResponseCookie expirationCookie = jwtTokenProvider.generateRefreshTokenCookieExpiration();
    response.addHeader(HttpHeaders.SET_COOKIE, expirationCookie.toString());

    // refresh token 추출
    String requestCookie = extractRefreshToken(request);
    if (requestCookie != null) {
      UUID userId = UUID.fromString(jwtTokenProvider.getUserId(requestCookie));
      jwtRegistry.invalidateJwtInformationByUserId(userId);
    }

    log.debug("로그아웃 성공");

    // 캐시 삭제
    Optional.ofNullable(cacheManager.getCache(CacheNames.USER_CACHE))
        .ifPresent(Cache::clear);
  }

  private String extractRefreshToken(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies == null) {
      return null;
    }

    return Arrays.stream(cookies)
        .filter(cookie -> cookie.getName().equals(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME))
        .map(Cookie::getValue)
        .findFirst()
        .orElse(null);
  }
}
