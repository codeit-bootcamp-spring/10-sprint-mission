package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.message.UserUpdatedEvent;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final ApplicationEventPublisher eventPublisher;

  @Override
  public void logout(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    // refresh token 쿠키 삭제
    Cookie refreshTokenExpirationCookie = tokenProvider.genereateRefreshTokenExpirationCookie();
    response.addCookie(refreshTokenExpirationCookie);

    Cookie[] cookies = request.getCookies();

    if (cookies == null) {
      log.debug("JWT logout handler executed - no cookies");
      return;
    }

    Arrays.stream(cookies)
        .filter(cookie -> cookie.getName().equals(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME))
        .findFirst()
        .ifPresent(
            cookie -> {
              String refreshToken = cookie.getValue();
              UUID userId = tokenProvider.getUserId(refreshToken);

              Optional<User> userOptional = userRepository.findById(userId);

              // 아직 registry 무효화 전이므로 online=true 상태
              UserDto before = userOptional.map(userMapper::toDto).orElse(null);

              jwtRegistry.invalidateJwtInformationByUserId(userId);

              // registry 무효화 후이므로 online=false 상태
              UserDto after = userOptional.map(userMapper::toDto).orElse(null);

              if (before != null && after != null) {
                eventPublisher.publishEvent(new UserUpdatedEvent(before, after, Instant.now()));
              }
            });

    log.debug("JWT logout handler executed - refresh token cookie cleared");
  }
}
