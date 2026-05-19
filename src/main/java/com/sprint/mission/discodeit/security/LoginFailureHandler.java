package com.sprint.mission.discodeit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoginFailureHandler implements AuthenticationFailureHandler {

  private final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException exception) throws IOException, ServletException {

    response.setStatus(401);
    response.setContentType("application/json;charset=UTF-8");

    log.debug("로그인 실패 유저 이름: username={}", request.getParameter("username"));

    ErrorResponse errorResponse = ErrorResponse.of(
        Instant.now(),
        ErrorCode.PASSWORD_MISMATCH.name(),
        exception.getMessage(),
        Map.of(),
        exception.getClass().getSimpleName(),
        401
    );
    String json = objectMapper.writeValueAsString(errorResponse);

    response.getWriter().write(json);
  }
}
