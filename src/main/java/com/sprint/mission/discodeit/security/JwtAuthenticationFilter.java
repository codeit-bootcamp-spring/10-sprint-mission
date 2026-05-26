package com.sprint.mission.discodeit.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final DiscodeitUserDetailsService discodeitUserDetailsService;
  private final JwtRegistry jwtRegistry;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    // 1. 요청 헤더에서 JWT 토큰 추출
    String token = resolveToken(request);

    // 2. 토큰이 유효한지 검증
    if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
      // Registry 검사 통과 시에만 인증 주입
      if (jwtRegistry.hasActiveJwtInformationByAccessToken(token)) {
        String subject = jwtTokenProvider.getSubjectFromToken(token);
        UUID userId = UUID.fromString(subject);
        UserDetails userDetails = discodeitUserDetailsService.loadUserById(userId);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
      } else {
        // 서명은 맞지만 Registry에 없는 토큰 예외
        log.warn("Valid token provided but not found in JwtRegistry. accessToken: {}", token);
      }
    }

    // 3. 다음 필터로 이동
    filterChain.doFilter(request, response);
  }

  // HTTP 요청에서 JWT 토큰 값만 추출하는 메서드
  private String resolveToken(HttpServletRequest request) {
    // 1. 요청 헤더에서 Authorization 키값을 가진 데이터 전체를 추출
    String bearerToken = request.getHeader("Authorization");

    // 2. 추출한 데이터가 "Bearer "로 시작하는 경우, "Bearer " 이후의 토큰 문자열만 반환
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }

    // 3. 헤더가 없거나 형식이 틀렸다면 null을 반환해서 다음 필터로 넘어가게 둠
    return null;
  }
}
