package com.sprint.mission.discodeit.security.handler;

import com.sprint.mission.discodeit.entity.RefreshToken;
import com.sprint.mission.discodeit.event.UserOnlineStatusChangedEvent;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.service.auth.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

  private final JwtRegistry jwtRegistry;
  private final RefreshTokenService refreshTokenService;
  private final CacheManager cacheManager;
  private final ApplicationEventPublisher eventPublisher;

  @Override
  public void logout(HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication) {

    if (request.getCookies() == null) {
      return;
    }

    for (Cookie cookie : request.getCookies()) {

      if (!"REFRESH_TOKEN".equals(cookie.getName())) {
        continue;
      }

      String refreshTokenValue = cookie.getValue();

      try {
        RefreshToken refreshToken =
            refreshTokenService.findByToken(refreshTokenValue);

        UUID userId = refreshToken.getUserId();

        jwtRegistry.invalidateJwtInformationByUserId(userId);

        eventPublisher.publishEvent(new UserOnlineStatusChangedEvent(userId));

        refreshTokenService.deleteByToken(refreshTokenValue);

        Cache cache = cacheManager.getCache("users");

        if (cache != null) {
          cache.clear();
        }

      } catch (Exception e) {
        System.err.println("Logout 무시됨: " + e.getMessage());
      }

      Cookie deleteCookie = new Cookie("REFRESH_TOKEN", null);
      deleteCookie.setHttpOnly(true);
      deleteCookie.setPath("/");
      deleteCookie.setMaxAge(0);

      response.addCookie(deleteCookie);
    }
  }
}
