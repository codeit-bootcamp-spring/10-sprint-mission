package com.sprint.mission.discodeit.auth.infra.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.auth.infra.web.CookieProvider;
import com.sprint.mission.discodeit.auth.presentation.dto.JwtLoginResponse;
import com.sprint.mission.discodeit.auth.application.service.JwtTokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final ObjectMapper objectMapper;
  private final CookieProvider cookieProvider;
  private final JwtTokenService jwtTokenService;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    String device = request.getHeader("X-Device-Id");
    JwtLoginResponse result = jwtTokenService.createJwtLogin(authentication.getName(), device);
    String json = objectMapper.writeValueAsString(result);
    response.addHeader(HttpHeaders.SET_COOKIE,
        cookieProvider.createRefreshTokenCookie(result.refreshToken()).toString());
    response.getWriter().write(json);
    log.info("login - [user - {}]", authentication.getName());
  }
}
