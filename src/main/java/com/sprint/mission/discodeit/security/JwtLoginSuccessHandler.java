package com.sprint.mission.discodeit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.jwt.JwtDto;
import com.sprint.mission.discodeit.dto.jwt.JwtInformation;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.registry.JwtRegistry;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    response.setContentType("application/json;charset=UTF-8");

    if (authentication != null &&
        authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
      response.setStatus(200);
      UserDto userDto = userDetails.getUserDto();
      String accessToken = delegateAccessToken(userDto);
      String refreshToken = delegateRefreshToken(userDto);

      JwtDto jwtDto = new JwtDto(userDto, accessToken);
      String json = objectMapper.writeValueAsString(jwtDto);
      response.getWriter().write(json);

      JwtInformation jwtInformation = new JwtInformation(userDto, accessToken, refreshToken);
      jwtRegistry.registerJwtInformation(jwtInformation);

      Cookie refreshTokenCookie = new Cookie(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME,
          refreshToken);
      refreshTokenCookie.setHttpOnly(true);
      refreshTokenCookie.setPath("/");
      refreshTokenCookie.setMaxAge(60 * 60 * 24 * 7);
      response.addCookie(refreshTokenCookie);

      log.debug("[LOGIN] 로그인 성공: username={}", userDetails.getUsername());
    } else {
      Object principal = authentication.getPrincipal();
      String principalType = (principal != null) ? principal.getClass().getName() : "null";
      log.error("[LOGIN] 인증 객체 타입이 불일치: class={}", principalType);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "인증 시스템 내부 오류");
    }
  }

  private String delegateAccessToken(UserDto userDto) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("username", userDto.username());
    claims.put("email", userDto.email());
    claims.put("roles", userDto.role().getDbKey());
    claims.put("userId", userDto.id());

    String subject = userDto.id().toString();

    return jwtTokenProvider.generateAccessToken(
        claims, subject);
  }

  private String delegateRefreshToken(UserDto userDto) {
    String subject = userDto.id().toString();
    return jwtTokenProvider.generateRefreshToken(subject);
  }
}
