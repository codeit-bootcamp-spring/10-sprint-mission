package com.sprint.mission.discodeit.global.security.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.auth.domain.provider.JwtRegistry;
import com.sprint.mission.discodeit.global.security.exception.JwtAuthenticationErrorCode;
import com.sprint.mission.discodeit.global.security.exception.JwtAuthenticationException;
import com.sprint.mission.discodeit.global.security.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

class JwtAuthenticationFilterTest {

  private JwtTokenProvider jwtTokenProvider;
  private JwtRegistry jwtRegistry;
  private JwtAuthenticationFilter filter;

  @BeforeEach
  void setUp() {
    jwtTokenProvider = mock(JwtTokenProvider.class);
    jwtRegistry = mock(JwtRegistry.class);
    filter = new JwtAuthenticationFilter(jwtTokenProvider, jwtRegistry);
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("유효한 토큰을 제공하면 인증에 성공하고 필터 체인을 계속 진행한다.")
  void doFilterInternal_validToken() throws ServletException, IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    FilterChain filterChain = mock(FilterChain.class);

    given(request.getHeader("Authorization")).willReturn("Bearer valid-token");
    given(request.getHeader("X-Device-Id")).willReturn("test-device");

    Authentication auth = mock(Authentication.class);
    given(auth.getName()).willReturn(UUID.randomUUID().toString());
    given(jwtTokenProvider.getAuthentication("valid-token")).willReturn(auth);
    given(jwtRegistry.isActiveSession(any(UUID.class), anyString())).willReturn(true);

    filter.doFilterInternal(request, response, filterChain);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isEqualTo(auth);
    verify(filterChain).doFilter(request, response);
  }

  @Test
  @DisplayName("토큰이 없으면 필터 체인을 계속 진행한다.")
  void doFilterInternal_noToken() throws ServletException, IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    FilterChain filterChain = mock(FilterChain.class);

    given(request.getHeader("Authorization")).willReturn(null);

    filter.doFilterInternal(request, response, filterChain);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    verify(filterChain).doFilter(request, response);
  }

  @Test
  @DisplayName("잘못된 토큰을 제공하면 예외가 발생한다.")
  void doFilterInternal_invalidToken() throws ServletException, IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    FilterChain filterChain = mock(FilterChain.class);

    given(request.getHeader("Authorization")).willReturn("Bearer invalid-token");
    given(jwtTokenProvider.getAuthentication("invalid-token"))
        .willThrow(new JwtAuthenticationException(JwtAuthenticationErrorCode.INVALID_TOKEN));

    assertThatThrownBy(() -> filter.doFilterInternal(request, response, filterChain))
        .isInstanceOf(JwtAuthenticationException.class);
  }
}
