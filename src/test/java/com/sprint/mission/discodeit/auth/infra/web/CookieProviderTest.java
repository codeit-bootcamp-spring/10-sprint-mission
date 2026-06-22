package com.sprint.mission.discodeit.auth.infra.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.sprint.mission.discodeit.global.security.jwt.JwtProperties;
import com.sprint.mission.discodeit.global.security.jwt.JwtProperties.CookieProperties;
import com.sprint.mission.discodeit.global.security.jwt.JwtProperties.RefreshToken;
import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;

@ExtendWith(MockitoExtension.class)
class CookieProviderTest {

  @Mock
  private JwtProperties jwtProperties;

  @InjectMocks
  private CookieProvider cookieProvider;

  @Test
  @DisplayName("createRefreshTokenCookie - 설정값을 기반으로 리프레시 토큰 쿠키를 생성한다.")
  void createRefreshTokenCookie() {
    RefreshToken refreshProps = mock(RefreshToken.class);
    CookieProperties cookieProps = mock(CookieProperties.class);

    given(jwtProperties.refreshToken()).willReturn(refreshProps);
    given(refreshProps.cookie()).willReturn(cookieProps);

    given(cookieProps.name()).willReturn("REFRESH_TOKEN");
    given(cookieProps.path()).willReturn("/auth");
    given(cookieProps.secure()).willReturn(true);
    given(cookieProps.sameSite()).willReturn("None");
    given(refreshProps.expiration()).willReturn(604800L);

    ResponseCookie cookie = cookieProvider.createRefreshTokenCookie("token-value");

    assertThat(cookie.getName()).isEqualTo("REFRESH_TOKEN");
    assertThat(cookie.getValue()).isEqualTo("token-value");
    assertThat(cookie.getPath()).isEqualTo("/auth");
    assertThat(cookie.getMaxAge()).isEqualTo(Duration.ofSeconds(604800L));
    assertThat(cookie.isHttpOnly()).isTrue();
    assertThat(cookie.isSecure()).isTrue();
    assertThat(cookie.getSameSite()).isEqualTo("None");
  }

  @Test
  @DisplayName("createDeleteCookie - 쿠키를 삭제하기 위해 만료시간을 0으로 설정하여 쿠키를 생성한다.")
  void createDeleteCookie() {
    RefreshToken refreshProps = mock(RefreshToken.class);
    CookieProperties cookieProps = mock(CookieProperties.class);

    given(jwtProperties.refreshToken()).willReturn(refreshProps);
    given(refreshProps.cookie()).willReturn(cookieProps);

    given(cookieProps.name()).willReturn("REFRESH_TOKEN");
    given(cookieProps.path()).willReturn("/auth");
    given(cookieProps.secure()).willReturn(true);
    given(cookieProps.sameSite()).willReturn("None");

    ResponseCookie cookie = cookieProvider.createDeleteCookie();

    assertThat(cookie.getName()).isEqualTo("REFRESH_TOKEN");
    assertThat(cookie.getValue()).isEmpty();
    assertThat(cookie.getPath()).isEqualTo("/auth");
    assertThat(cookie.getMaxAge()).isEqualTo(Duration.ZERO);
    assertThat(cookie.isHttpOnly()).isTrue();
    assertThat(cookie.isSecure()).isTrue();
    assertThat(cookie.getSameSite()).isEqualTo("None");
  }

  @Test
  @DisplayName("getRefreshTokenName - 리프레시 토큰의 쿠키 이름을 반환한다.")
  void getRefreshTokenName() {
    RefreshToken refreshProps = mock(RefreshToken.class);
    CookieProperties cookieProps = mock(CookieProperties.class);

    given(jwtProperties.refreshToken()).willReturn(refreshProps);
    given(refreshProps.cookie()).willReturn(cookieProps);
    given(cookieProps.name()).willReturn("REFRESH_TOKEN");

    String name = cookieProvider.getRefreshTokenName();

    assertThat(name).isEqualTo("REFRESH_TOKEN");
  }
}
