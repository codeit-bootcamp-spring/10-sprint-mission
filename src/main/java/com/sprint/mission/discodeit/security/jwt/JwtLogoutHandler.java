package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.service.SseService;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

  private final JwtTokenProvider tokenProvider;
  private final JwtRegistry jwtRegistry;
  private final UserService userService;
  private final SseService sseService;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {

    // Clear refresh token cookie
    Cookie refreshTokenExpirationCookie = tokenProvider.genereateRefreshTokenExpirationCookie();
    response.addCookie(refreshTokenExpirationCookie);

    if (request.getCookies() != null) {
      Arrays.stream(request.getCookies())
          .filter(cookie -> cookie.getName().equals(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME))
          .findFirst()
          .ifPresent(cookie -> {
            String refreshToken = cookie.getValue();
            UUID userId = tokenProvider.getUserId(refreshToken);
            jwtRegistry.invalidateJwtInformationByUserId(userId);

            try {
              UserDto userDto = userService.find(userId);
              sseService.broadcast("users.updated", userDto);
            } catch (Exception e) {
              log.error("Failed to broadcast logout status for user {}", userId, e);
            }
          });
    }

    log.debug("JWT logout handler executed - refresh token cookie cleared");
  }
}