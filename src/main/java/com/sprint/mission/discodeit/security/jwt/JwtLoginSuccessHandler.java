package com.sprint.mission.discodeit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.response.JwtDto;
import com.sprint.mission.discodeit.event.sse.UserChangedEvent;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

  private final JwtTokenProvider jwtTokenProvider;
  private final JwtProperties jwtProperties;
  private final ObjectMapper objectMapper;
  private final JwtRegistry jwtRegistry;
  private final CacheManager cacheManager;
  private final ApplicationEventPublisher applicationEventPublisher;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication
  ) throws IOException, ServletException {
    UserDto userDto = ((DiscodeitUserDetails) authentication.getPrincipal()).getUserDto();

    String accessToken = jwtTokenProvider.generateAccessToken(userDto);
    String refreshToken = jwtTokenProvider.generateRefreshToken(userDto);

    jwtRegistry.registerJwtInformation(new JwtInformation(userDto, accessToken, refreshToken));

    Cookie cookie = new Cookie(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, refreshToken);
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setMaxAge((int) (jwtProperties.refreshTokenExpiry() / 1000));
    response.addCookie(cookie);

    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");
    objectMapper.writeValue(response.getWriter(), new JwtDto(userDto, accessToken));

    Cache userCache = cacheManager.getCache("users");
    if (userCache != null) {
      userCache.clear();
    }

    UserDto onlineUser = new UserDto(userDto.id(), userDto.username(), userDto.email(),
        userDto.profile(), true, userDto.role());
    applicationEventPublisher.publishEvent(
        new UserChangedEvent(UserChangedEvent.Type.STATUS_CHANGED, onlineUser));
  }
}
