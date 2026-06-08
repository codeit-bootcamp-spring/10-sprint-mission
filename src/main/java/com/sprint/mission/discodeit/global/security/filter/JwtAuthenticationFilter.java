package com.sprint.mission.discodeit.global.security.filter;

import com.sprint.mission.discodeit.global.security.exception.JwtAuthenticationErrorCode;
import com.sprint.mission.discodeit.global.security.exception.JwtAuthenticationException;
import com.sprint.mission.discodeit.global.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.global.security.jwt.registry.JwtRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String token = parseToken(request);
    if (!StringUtils.hasText(token)) {
      filterChain.doFilter(request, response);
      return;
    }
    jwtTokenProvider.validate(token);
    Authentication authToken = jwtTokenProvider.getAuthentication(token);
    String device = request.getHeader("X-Device-Id");
    if (!jwtRegistry.isActiveSession(UUID.fromString(authToken.getName()), device)) {
      throw new JwtAuthenticationException(JwtAuthenticationErrorCode.ACTIVE_DEVICE_CHANGED);
    }
    SecurityContextHolder.getContext().setAuthentication(authToken);
    filterChain.doFilter(request, response);
  }

  private String parseToken(HttpServletRequest request) {
    String header = request.getHeader("Authorization");
    if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
      return header.substring(7);
    }
    return null;
  }
}
