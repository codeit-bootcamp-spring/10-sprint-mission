package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.event.sse.UserChangedEvent;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
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
  private final JwtTokenProvider jwtTokenProvider;
  private final CacheManager cacheManager;
  private final ApplicationEventPublisher applicationEventPublisher;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {
    if (request.getCookies() != null) {
      Arrays.stream(request.getCookies())
          .filter(c -> JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME.equals(c.getName()))
          .findFirst()
          .ifPresent(c -> {
            try {
              UserDto userDto = jwtTokenProvider.extractUserDtoFromRefreshToken(c.getValue());
              jwtRegistry.invalidateJwtInformationByUserId(userDto.id());
              UserDto offlineUser = new UserDto(userDto.id(), userDto.username(), userDto.email(),
                  userDto.profile(), false, userDto.role());
              applicationEventPublisher.publishEvent(
                  new UserChangedEvent(UserChangedEvent.Type.STATUS_CHANGED, offlineUser));
            } catch (Exception ignored) {
            }
          });
    }

    Cookie cookie = new Cookie(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, null);
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setMaxAge(0);
    response.addCookie(cookie);

    Cache userCache = cacheManager.getCache("users");
    if (userCache != null) {
      userCache.clear();
    }
  }
}
