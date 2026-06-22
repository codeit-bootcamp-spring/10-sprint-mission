package com.sprint.mission.discodeit.global.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.global.security.exception.JwtAuthenticationException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

class JwtTokenProviderTest {

  private JwtTokenProvider jwtTokenProvider;

  @BeforeEach
  void setUp() throws JOSEException {
    JwtProperties jwtProperties = mock(JwtProperties.class);
    when(jwtProperties.secretKey()).thenReturn("a-very-long-secret-key-for-testing-jwt-token-provider");
    
    JwtProperties.Token accessToken = mock(JwtProperties.Token.class);
    when(accessToken.expiration()).thenReturn(3600L); // 1 hour
    when(jwtProperties.accessToken()).thenReturn(accessToken);

    JwtProperties.RefreshToken refreshToken = mock(JwtProperties.RefreshToken.class);
    when(refreshToken.expiration()).thenReturn(86400L); // 1 day
    when(jwtProperties.refreshToken()).thenReturn(refreshToken);

    jwtTokenProvider = new JwtTokenProvider(jwtProperties);
  }

  @Test
  @DisplayName("generateAccessToken - 엑세스 토큰을 생성한다.")
  void generateAccessToken() {
    UUID subject = UUID.randomUUID();
    String authorities = "USER";

    String token = jwtTokenProvider.generateAccessToken(subject, authorities);

    assertThat(token).isNotBlank();
    jwtTokenProvider.validate(token);
  }

  @Test
  @DisplayName("generateRefreshToken - 리프레시 토큰을 생성한다.")
  void generateRefreshToken() {
    UUID subject = UUID.randomUUID();

    String token = jwtTokenProvider.generateRefreshToken(subject);

    assertThat(token).isNotBlank();
    jwtTokenProvider.validate(token);
  }

  @Test
  @DisplayName("getAuthentication - 토큰에서 인증 객체를 추출한다.")
  void getAuthentication() {
    UUID subject = UUID.randomUUID();
    String authorities = "USER";
    String token = jwtTokenProvider.generateAccessToken(subject, authorities);

    Authentication auth = jwtTokenProvider.getAuthentication(token);

    assertThat(auth).isNotNull();
    assertThat(auth.getName()).isEqualTo(subject.toString());
    assertThat(auth.getAuthorities()).hasSize(1);
    assertThat(auth.getAuthorities().iterator().next().getAuthority()).isEqualTo("USER");
  }

  @Test
  @DisplayName("validate - 잘못된 토큰이면 예외가 발생한다.")
  void validate_invalidToken() {
    String invalidToken = "invalid-token";

    assertThatThrownBy(() -> jwtTokenProvider.validate(invalidToken))
        .isInstanceOf(JwtAuthenticationException.class);
  }

  @Test
  @DisplayName("getAuthentication - 권한이 없는 토큰이면 예외가 발생한다.")
  void getAuthentication_noAuthorities() {
    UUID subject = UUID.randomUUID();
    String token = jwtTokenProvider.generateRefreshToken(subject);

    assertThatThrownBy(() -> jwtTokenProvider.getAuthentication(token))
        .isInstanceOf(JwtAuthenticationException.class);
  }
}
