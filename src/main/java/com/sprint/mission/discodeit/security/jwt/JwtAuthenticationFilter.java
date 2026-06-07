package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

// JWT 인증 필터 - 모든 HTTP 요청에서 JWT를 검사하고 SecurityContext에 인증 정보를 등록
// OncePerRequestFilter를 상속해 요청당 정확히 한 번만 실행됨을 보장
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final String BEARER_PREFIX = "Bearer ";

  private final JwtTokenProvider jwtTokenProvider;
  private final UserDetailsService userDetailsService;
  private final JwtRegistry jwtRegistry;

  /*
   * 처리 흐름:
   *   1. Authorization 헤더에서 Bearer 토큰 추출
   *   2. 토큰 없음 -> 인증 없이 다음 필터로 통과 (공개 API는 Security 설정에서 따로 허용)
   *   3. 토큰 유효성 검사 실패 -> 401 즉시 반환
   *   4. 토큰 유효 -> SecurityContext에 인증 정보 등록 후 다음 필터로 통과
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    // Authorization 헤더 추출
    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

    // Bearer 토큰 없으면 인증 시도 생략
    if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
      filterChain.doFilter(request, response); // 현재 필터를 건너뛰고 다음 필터로 요청을 넘김
      return;
    }

    // Bearer 이후의 실제 토큰 문자열만 추출
    String token = authHeader.substring(BEARER_PREFIX.length());

    // 토큰 유효성 검사 실패 -> 401 즉시 반환
    if (!jwtTokenProvider.validateToken(token)) {
      sendUnauthorized(response);
      return; // 필터 체인 진행 중단 -> 컨트롤러까지 요청이 도달하지 않음
    }

    // registry에 없는 토큰 (로그아웃/강제 무효화) -> 401 즉시 반환
    if (!jwtRegistry.hasActiveJwtInformationByAccessToken(token)) {
      sendUnauthorized(response);
      return;
    }

    // 유효한 토큰 -> SecurityContext에 인증 주입
    String username = jwtTokenProvider.extractUsername(token);
    // DB 또는 캐시에서 실제 UserDetails 로드 (최신 정보 반영)
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

    // SecurityContext에 인증 객체 등록
    // UsernamePasswordAuthenticationToken 생성
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(
            userDetails, // principal  (누구인지)
            null, // credentials (비밀번호 — 이미 검증했으므로 null)
            userDetails.getAuthorities() // authorities (권한 목록)
        );
    // 현재 HTTP 요청의 부가 정보(IP 주소, 세션 ID)를 인증 객체에 추가 - 보안 감사 로그나 추가 검증에 활용
    authentication.setDetails(
        new WebAuthenticationDetailsSource().buildDetails(request));
    // SecurityContext에 인증 정보 저장
    // -> 이후 컨트롤러에서 @AuthenticationPrincipal, SecurityContextHolder.getContext()로 꺼낼 수 있음
    SecurityContextHolder.getContext().setAuthentication(authentication);

    filterChain.doFilter(request, response); // 다음 필터로 요청 전달
  }

  private void sendUnauthorized(HttpServletResponse response) throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write(
        "{\"code\":\"INVALID_TOKEN\",\"message\":\""
            + ErrorCode.INVALID_TOKEN.getMessage() + "\"}");
  }
}
