package com.sprint.mission.discodeit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.user.UserDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

  private final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    response.setStatus(200);
    response.setContentType("application/json;charset=UTF-8");

    if (authentication != null &&
        authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
      UserDto userDto = userDetails.getUserDto();
      String json = objectMapper.writeValueAsString(userDto);
      response.getWriter().write(json);
      log.debug("로그인 성공: username={}", userDetails.getUsername());
    } else {
      Object principal = authentication.getPrincipal();
      String principalType = (principal != null) ? principal.getClass().getName() : "null";
      log.error("인증 객체 타입이 불일치: class={}", principalType);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "인증 시스템 내부 오류");
    }

  }
}
