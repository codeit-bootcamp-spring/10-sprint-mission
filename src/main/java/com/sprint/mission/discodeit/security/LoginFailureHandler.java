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
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginFailureHandler implements AuthenticationFailureHandler {

  private final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse res,
      AuthenticationException ex) throws IOException, ServletException {
    ErrorResponse body = new ErrorResponse(
        Instant.now(),
        ErrorCode.INVALID_USER_CREDENTIALS.name(),
        ErrorCode.INVALID_USER_CREDENTIALS.getMessage(),
        Map.of(),
        ex.getClass().getSimpleName(),
        HttpServletResponse.SC_UNAUTHORIZED
    );
    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    res.setContentType(MediaType.APPLICATION_JSON_VALUE);
    res.setCharacterEncoding("UTF-8");
    objectMapper.writeValue(res.getWriter(), body);
  }
}
