package com.sprint.mission.discodeit.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HttpStatusReturningLogoutSuccessHandler implements LogoutSuccessHandler {

  @Override
  public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    response.setStatus(204);
    if (authentication != null
        && authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
      log.debug("[LOGOUT] 로그아웃 성공: username={}", userDetails.getUsername());
    } else {
      log.debug("[LOGOUT] 이미 세션이 만료되었거나 인증되지 않은 사용자가 로그아웃을 시도");
    }
  }
}
