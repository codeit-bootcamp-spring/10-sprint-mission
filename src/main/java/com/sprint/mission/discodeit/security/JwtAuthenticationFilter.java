package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.exception.user.DiscodeitUnauthorizedException;
import com.sprint.mission.discodeit.registry.JwtRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    try {
      Map<String, Object> claims = verifyJws(request);
      setAuthenticationToContext(claims);
    } catch (Exception e) {
      log.error("JWT 필터 예외 발생 - 요청 URL: {}, 원인: {}", request.getRequestURI(), e.getMessage(), e);
      SecurityContextHolder.clearContext();
    }
    filterChain.doFilter(request, response);
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    String authorization = request.getHeader("Authorization");
    return authorization == null || !authorization.startsWith("Bearer ");
  }

  private Map<String, Object> verifyJws(HttpServletRequest request) {
    String jws = request.getHeader("Authorization").substring(7);
    if (!jwtRegistry.hasActiveJwtInformationByAccessToken(jws)) {
      throw new DiscodeitUnauthorizedException();
    }
    return jwtTokenProvider.getClaims(jws);
  }

  private void setAuthenticationToContext(Map<String, Object> claims) {
    UUID userId = UUID.fromString(claims.get("sub").toString());
    String username = claims.get("username").toString();
    String email = claims.get("email").toString();
    Role role = Role.fromString(claims.get("roles").toString());

    UserDto userDto = new UserDto(
        userId,
        username,
        email,
        null,
        true,
        role
    );

    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDto, null);

    Authentication authentication = new UsernamePasswordAuthenticationToken(
        userDetails, null, userDetails.getAuthorities()
    );
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }
}
