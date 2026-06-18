package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.event.user.UserUpdateEvent;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

  private final JwtTokenProvider tokenProvider;
  private final JwtRegistry jwtRegistry;
  private final CacheManager cacheManager;

  private final ApplicationEventPublisher eventPublisher;
  private final UserRepository userRepository;
  private final UserMapper userMapper;

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

                  userRepository.findById(userId)
                          .map(userMapper::toDto)
                          .ifPresent(userDto ->
                                  eventPublisher.publishEvent(new UserUpdateEvent(userDto))
                          );
              });

      ///로그아웃시, 사용자 목록 캐시 무효화.
      Cache cache = cacheManager.getCache("users");
      if (cache != null) {
          cache.evict("all");
      }

    log.debug("JWT logout handler executed - refresh token cookie cleared");
  }
}