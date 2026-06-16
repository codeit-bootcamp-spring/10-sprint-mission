package com.sprint.mission.discodeit.security.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.jwt.JwtDto;
import com.sprint.mission.discodeit.dto.jwt.JwtInformation;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.event.sse.UserChangedEvent;
import com.sprint.mission.discodeit.event.sse.UserChangedEvent.Action;
import com.sprint.mission.discodeit.registry.JwtRegistry;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

  private final JwtTokenProvider jwtTokenProvider;
  private final ObjectMapper objectMapper;
  private final JwtRegistry jwtRegistry;
  private final CacheManager cacheManager;
  private final ApplicationEventPublisher eventPublisher;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    response.setContentType("application/json;charset=UTF-8");

    if (authentication != null &&
        authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
      response.setStatus(200);
      UserDto userDto = userDetails.getUserDto();
      String accessToken = jwtTokenProvider.delegateAccessToken(userDto);
      String refreshToken = jwtTokenProvider.delegateRefreshToken(userDto);

      JwtDto jwtDto = new JwtDto(userDto, accessToken);
      String json = objectMapper.writeValueAsString(jwtDto);
      response.getWriter().write(json);

      JwtInformation jwtInformation = new JwtInformation(userDto, accessToken, refreshToken);
      jwtRegistry.registerJwtInformation(jwtInformation);

      ResponseCookie expiredCookie = ResponseCookie.from(
              JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, refreshToken)
          .httpOnly(true)
          .path("/")
          .maxAge(60 * 60 * 24 * 7)
          .sameSite("Strict")
          .build();
      response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());
      Cache usersCache = cacheManager.getCache("users");
      if (usersCache != null) {
        usersCache.clear();
        log.debug("[CACHE] 유저 로그인 성공 users 캐시 초기화 완료");
      }
      log.debug("[LOGIN] 로그인 성공: username={}", userDetails.getUsername());
      eventPublisher.publishEvent(new UserChangedEvent(userDto, Action.UPDATED));
    } else {
      Object principal = authentication.getPrincipal();
      String principalType = (principal != null) ? principal.getClass().getName() : "null";
      log.error("[LOGIN] 인증 객체 타입이 불일치: class={}", principalType);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "인증 시스템 내부 오류");
    }
  }
}
