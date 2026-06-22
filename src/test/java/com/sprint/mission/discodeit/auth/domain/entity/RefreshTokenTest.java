package com.sprint.mission.discodeit.auth.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.sprint.mission.discodeit.user.domain.entity.User;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RefreshTokenTest {

  @Test
  @DisplayName("rotate - 새로운 토큰과 만료 시간으로 업데이트하고, 이전 토큰을 저장한다.")
  void rotate_success() {
    User user = mock(User.class);
    Instant now = Instant.now();
    RefreshToken refreshToken = RefreshToken.builder()
        .user(user)
        .device("mobile")
        .token("oldToken")
        .expiresAt(now.plus(1, ChronoUnit.DAYS))
        .build();

    Instant newExpiresAt = now.plus(7, ChronoUnit.DAYS);
    refreshToken.rotate("newToken", newExpiresAt);

    assertThat(refreshToken.getToken()).isEqualTo("newToken");
    assertThat(refreshToken.getPreviousToken()).isEqualTo("oldToken");
    assertThat(refreshToken.getExpiresAt()).isEqualTo(newExpiresAt);
  }

  @Test
  @DisplayName("rotate - 기존 토큰과 동일한 토큰으로 교체하려 하면 예외가 발생한다.")
  void rotate_sameToken_throwsException() {
    User user = mock(User.class);
    Instant now = Instant.now();
    RefreshToken refreshToken = RefreshToken.builder()
        .user(user)
        .device("mobile")
        .token("sameToken")
        .expiresAt(now.plus(1, ChronoUnit.DAYS))
        .build();

    assertThatThrownBy(() -> refreshToken.rotate("sameToken", now.plus(7, ChronoUnit.DAYS)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("existing token is same new token");
  }

  @Test
  @DisplayName("isExpired - 현재 시간이 만료 시간을 지났으면 true를 반환한다.")
  void isExpired() {
    User user = mock(User.class);
    Instant now = Instant.now();
    RefreshToken refreshToken = RefreshToken.builder()
        .user(user)
        .device("mobile")
        .token("token")
        .expiresAt(now.minus(1, ChronoUnit.SECONDS))
        .build();

    assertThat(refreshToken.isExpired(now)).isTrue();
  }

  @Test
  @DisplayName("hasToken - 현재 토큰이 일치하면 true를 반환한다.")
  void hasToken_currentToken() {
    User user = mock(User.class);
    RefreshToken refreshToken = RefreshToken.builder()
        .user(user)
        .device("mobile")
        .token("currentToken")
        .expiresAt(Instant.now().plus(1, ChronoUnit.DAYS))
        .build();

    assertThat(refreshToken.hasToken("currentToken")).isTrue();
  }

  @Test
  @DisplayName("hasToken - 이전 토큰이 일치하면 true를 반환한다.")
  void hasToken_previousToken() {
    User user = mock(User.class);
    RefreshToken refreshToken = RefreshToken.builder()
        .user(user)
        .device("mobile")
        .token("oldToken")
        .expiresAt(Instant.now().plus(1, ChronoUnit.DAYS))
        .build();

    refreshToken.rotate("newToken", Instant.now().plus(7, ChronoUnit.DAYS));

    assertThat(refreshToken.hasToken("oldToken")).isTrue();
  }

  @Test
  @DisplayName("isCurrentToken - 현재 토큰인지 확인한다.")
  void isCurrentToken() {
    User user = mock(User.class);
    RefreshToken refreshToken = RefreshToken.builder()
        .user(user)
        .device("mobile")
        .token("currentToken")
        .expiresAt(Instant.now().plus(1, ChronoUnit.DAYS))
        .build();

    assertThat(refreshToken.isCurrentToken("currentToken")).isTrue();
    assertThat(refreshToken.isCurrentToken("otherToken")).isFalse();
  }

  @Test
  @DisplayName("isCompromised - 이전 토큰과 일치하면 타협(탈취)된 것으로 간주한다.")
  void isCompromised() {
    User user = mock(User.class);
    RefreshToken refreshToken = RefreshToken.builder()
        .user(user)
        .device("mobile")
        .token("oldToken")
        .expiresAt(Instant.now().plus(1, ChronoUnit.DAYS))
        .build();

    refreshToken.rotate("newToken", Instant.now().plus(7, ChronoUnit.DAYS));

    assertThat(refreshToken.isCompromised("oldToken")).isTrue();
    assertThat(refreshToken.isCompromised("newToken")).isFalse();
  }
}
