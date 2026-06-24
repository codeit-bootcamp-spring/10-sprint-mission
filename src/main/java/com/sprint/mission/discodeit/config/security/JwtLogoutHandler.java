package com.sprint.mission.discodeit.config.security;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.service.SseService;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
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

  private final JwtRegistry jwtRegistry;
  private final JwtTokenProvider jwtTokenProvider;
  private final UserService userService;
  private final CacheManager cacheManager;
  private final SseService sseService;

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
          .ifPresent(cookie -> {
            try {
              UUID userId = jwtTokenProvider.getUserId(cookie.getValue());
              jwtRegistry.invalidateJwtInformationByRefreshToken(cookie.getValue());
              UserDto updatedUser = userService.find(userId);
              sseService.broadcast("users.updated", updatedUser);
            } catch (RuntimeException e) {
              log.debug("로그아웃 사용자 SSE 전송을 건너뜁니다.", e);
            }
          });
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
