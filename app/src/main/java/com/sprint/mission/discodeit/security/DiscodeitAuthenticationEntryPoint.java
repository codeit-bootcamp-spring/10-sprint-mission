package com.sprint.mission.discodeit.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
public class DiscodeitAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final HandlerExceptionResolver resolver;

  public DiscodeitAuthenticationEntryPoint(
      @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
    this.resolver = resolver;
  }

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException, ServletException {
    String acceptHeader = request.getHeader("Accept");
    boolean isSseRequest = "/api/sse".equals(request.getRequestURI()) ||
        (acceptHeader != null && acceptHeader.contains("text/event-stream"));

    if (isSseRequest) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized SSE Request");
      return;
    }
    resolver.resolveException(request, response, null, authException);
  }
}
