package com.sprint.mission.discodeit.security.filter;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.auth.DiscodeitUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String token = resolveToken(request);

    if (token != null) {
      try {
        Map<String, Object> claims = jwtTokenProvider.getClaims(token);

        if (!jwtRegistry.hasActiveJwtInformationByAccessToken(token)) {
          throw new DiscodeitException(ErrorCode.REVOKED_TOKEN);
        }

        setAuthenticationToContext(claims);

      } catch (DiscodeitException e) {
        SecurityContextHolder.clearContext();

        response.sendError(
            HttpServletResponse.SC_UNAUTHORIZED,
            e.getMessage()
        );

        return;
      }
    }

    filterChain.doFilter(request, response);
  }

  private String resolveToken(HttpServletRequest request) {
    String authorization = request.getHeader("Authorization");

    if (authorization != null && authorization.startsWith("Bearer ")) {
      return authorization.substring(7);
    }

    return null;
  }

  private void setAuthenticationToContext(Map<String, Object> claims) {

    String email = String.valueOf(claims.get("sub"));

    List<String> roles =
        claims.get("roles") == null
            ? List.of()
            : ((List<?>) claims.get("roles"))
                .stream()
                .map(String::valueOf)
                .toList();

    Object userIdClaim = claims.get("userId");

    UUID userId = userIdClaim == null
        ? null
        : UUID.fromString(String.valueOf(userIdClaim));

    List<GrantedAuthority> authorities = roles.stream()
        .map(role -> (GrantedAuthority) new SimpleGrantedAuthority(role))
        .toList();

    Role role = roles.isEmpty()
        ? null
        : Role.valueOf(roles.get(0).replace("ROLE_", ""));

    UserDto userDto = UserDto.builder()
        .id(userId)
        .email(email)
        .role(role)
        .build();

    DiscodeitUserDetails userDetails =
        new DiscodeitUserDetails(userDto, null);

    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            authorities
        );

    SecurityContextHolder.getContext()
        .setAuthentication(authentication);
  }
}
