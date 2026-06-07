package com.sprint.mission.discodeit.auth.filter;

import com.sprint.mission.discodeit.auth.jwt.JwtRegistry;
import com.sprint.mission.discodeit.auth.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final UserDetailsService userDetailsService;
  private final JwtRegistry jwtRegistry;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String token = getToken(request);
    if (token != null) {
      try {
        boolean validate = jwtTokenProvider.validateToken(token);
        boolean access = jwtRegistry.hasActiveJwtInformationByAccessToken(token);
        log.debug("validate: {}, access:{}", validate, access);

        if (!jwtTokenProvider.validateToken(token) ||
            !jwtRegistry.hasActiveJwtInformationByAccessToken(token)) {
          log.debug("토큰 검증 실패");
          throw new RuntimeException();
        }

        String username = jwtTokenProvider.getClaims(token).get("sub").toString();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
            );
        log.debug("JWT 인증 성공");
        SecurityContextHolder.getContext().setAuthentication(authentication);
      } catch (Exception e) {
        log.debug("JWT 인증 실패");
        SecurityContextHolder.clearContext();
      }
    }

    filterChain.doFilter(request, response);
  }

  private String getToken(HttpServletRequest request) {
    String authorization = request.getHeader("Authorization");
    if (authorization == null || !authorization.startsWith("Bearer ")) {
      return null;
    }

    return authorization.replaceAll("Bearer ", "");
  }
}
