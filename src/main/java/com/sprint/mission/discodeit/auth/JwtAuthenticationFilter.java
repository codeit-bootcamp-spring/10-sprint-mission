package com.sprint.mission.discodeit.auth;

import com.sprint.mission.discodeit.auth.jwt.JwtRegistry;
import com.sprint.mission.discodeit.auth.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.springframework.security.authentication.BadCredentialsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final UserRepository userRepository;
  private final DiscodeitUserDetailsService discodeitUserDetailsService;
  private final JwtRegistry jwtRegistry;
  private final AuthenticationEntryPoint authenticationEntryPoint;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String authorizationHeader = request.getHeader("Authorization");

    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      String accessToken = authorizationHeader.substring("Bearer ".length());

      try {

        if (jwtTokenProvider.validateToken(accessToken)
            && jwtRegistry.hasActiveJwtInformationByAccessToken(accessToken)) {

          UUID userId = UUID.fromString(jwtTokenProvider.getSubjectFromToken(accessToken));
          User user = userRepository.findById(userId).orElse(null);

          if (user == null) {
            throw new BadCredentialsException("토큰에 해당하는 사용자가 존재하지 않습니다.");
          }
          UserDetails userDetails = discodeitUserDetailsService.loadUserByUsername(
              user.getUsername());

          UsernamePasswordAuthenticationToken authentication =
              new UsernamePasswordAuthenticationToken(
                  userDetails,
                  null,
                  userDetails.getAuthorities()
              );
          SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
          throw new BadCredentialsException("유효하지 않거나 만료된 토큰입니다.");
        }
      } catch (Exception e) {
        authenticationEntryPoint.commence(request, response,
            new BadCredentialsException(e.getMessage(), e));
        return;
      }
    }
    filterChain.doFilter(request, response);
  }
}
