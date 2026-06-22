package com.sprint.mission.discodeit.auth.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.auth.domain.entity.RefreshToken;
import com.sprint.mission.discodeit.auth.domain.exception.AuthException;
import com.sprint.mission.discodeit.auth.domain.exception.JWTErrorCode;
import com.sprint.mission.discodeit.auth.domain.provider.AuthUserResolver;
import com.sprint.mission.discodeit.auth.domain.provider.JwtRegistry;
import com.sprint.mission.discodeit.auth.presentation.dto.JwtLoginResponse;
import com.sprint.mission.discodeit.auth.presentation.mapper.AuthMapper;
import com.sprint.mission.discodeit.global.security.jwt.JwtProperties;
import com.sprint.mission.discodeit.global.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.support.mapper.ApiMapperContainer;
import com.sprint.mission.discodeit.user.domain.entity.User;
import com.sprint.mission.discodeit.user.domain.entity.constant.UserRole;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JwtTokenServiceTest {

  @Mock
  private JwtProperties jwtProperties;
  @Mock
  private JwtTokenProvider jwtTokenProvider;
  @Mock
  private JwtRegistry jwtRegistry;
  @Mock
  private AuthUserResolver userResolver;

  @Spy
  private AuthMapper mapper = ApiMapperContainer.get(AuthMapper.class);

  @InjectMocks
  private JwtTokenService service;

  @Test
  @DisplayName("reissue - 정상적인 리프레시 토큰이면 새로운 토큰들을 발급한다.")
  void reissue_success() {
    String refreshToken = "valid.refresh.token";
    UUID userId = UUID.randomUUID();
    User user = mock(User.class);
    given(user.getId()).willReturn(userId);
    given(user.getRole()).willReturn(UserRole.USER);

    RefreshToken tokenEntity = mock(RefreshToken.class);
    given(tokenEntity.isCompromised(refreshToken)).willReturn(false);
    given(tokenEntity.isCurrentToken(refreshToken)).willReturn(true);
    given(tokenEntity.getUser()).willReturn(user);

    given(jwtRegistry.findByToken(refreshToken)).willReturn(tokenEntity);
    given(userResolver.getOrThrow(userId)).willReturn(user);

    String newAccess = "new.access";
    String newRefresh = "new.refresh";
    given(jwtTokenProvider.generateAccessToken(userId, UserRole.USER)).willReturn(newAccess);
    given(jwtTokenProvider.generateRefreshToken(userId)).willReturn(newRefresh);

    JwtProperties.RefreshToken refreshProps = mock(JwtProperties.RefreshToken.class);
    given(jwtProperties.refreshToken()).willReturn(refreshProps);
    given(refreshProps.expiration()).willReturn(3600L);

    JwtLoginResponse expectedResponse = new JwtLoginResponse(null, newAccess, newRefresh);
    given(mapper.toDtoFrom(user, newAccess, newRefresh)).willReturn(expectedResponse);

    JwtLoginResponse response = service.reissue(refreshToken);

    assertThat(response).isEqualTo(expectedResponse);
    verify(jwtTokenProvider).validate(refreshToken);
    verify(tokenEntity).rotate(eq(newRefresh), any());
    verify(jwtRegistry).register(tokenEntity);
  }

  @Test
  @DisplayName("reissue - 컴프로마이즈된 토큰이면 모든 토큰을 무효화하고 예외를 던진다.")
  void reissue_fail_compromised() {
    String refreshToken = "compromised.token";
    UUID userId = UUID.randomUUID();
    User user = mock(User.class);
    given(user.getId()).willReturn(userId);

    RefreshToken tokenEntity = mock(RefreshToken.class);
    given(tokenEntity.isCompromised(refreshToken)).willReturn(true);
    given(tokenEntity.getUser()).willReturn(user);

    given(jwtRegistry.findByToken(refreshToken)).willReturn(tokenEntity);

    assertThatThrownBy(() -> service.reissue(refreshToken))
        .isInstanceOf(AuthException.class)
        .hasFieldOrPropertyWithValue("errorCode", JWTErrorCode.INVALID_REFRESH_TOKEN);

    verify(jwtRegistry).invalidateAllByUserId(userId);
  }

  @Test
  @DisplayName("reissue - 현재 토큰이 아니면 예외를 던진다.")
  void reissue_fail_not_current() {
    String refreshToken = "old.refresh.token";

    RefreshToken tokenEntity = mock(RefreshToken.class);
    given(tokenEntity.isCompromised(refreshToken)).willReturn(false);
    given(tokenEntity.isCurrentToken(refreshToken)).willReturn(false);

    given(jwtRegistry.findByToken(refreshToken)).willReturn(tokenEntity);

    assertThatThrownBy(() -> service.reissue(refreshToken))
        .isInstanceOf(AuthException.class)
        .hasFieldOrPropertyWithValue("errorCode", JWTErrorCode.EXPIRED_TOKEN);
  }

  @Test
  @DisplayName("createJwtLogin - 새로운 로그인 시 토큰을 발급한다.")
  void createJwtLogin_success() {
    UUID userId = UUID.randomUUID();
    String device = "Mozilla/5.0";
    User user = mock(User.class);
    given(user.getId()).willReturn(userId);
    given(user.getRole()).willReturn(UserRole.USER);

    given(userResolver.getProxyOrThrow(userId)).willReturn(user);
    given(userResolver.getOrThrow(userId)).willReturn(user);

    String newAccess = "new.access";
    String newRefresh = "new.refresh";
    given(jwtTokenProvider.generateAccessToken(userId, UserRole.USER)).willReturn(newAccess);
    given(jwtTokenProvider.generateRefreshToken(userId)).willReturn(newRefresh);

    JwtProperties.RefreshToken refreshProps = mock(JwtProperties.RefreshToken.class);
    given(jwtProperties.refreshToken()).willReturn(refreshProps);
    given(refreshProps.expiration()).willReturn(3600L);

    JwtLoginResponse expectedResponse = new JwtLoginResponse(null, newAccess, newRefresh);
    given(mapper.toDtoFrom(user, newAccess, newRefresh)).willReturn(expectedResponse);

    JwtLoginResponse response = service.createJwtLogin(userId.toString(), device);

    assertThat(response).isEqualTo(expectedResponse);
    verify(jwtRegistry).register(any(RefreshToken.class));
  }

  @Test
  @DisplayName("deleteRefreshToken - 토큰을 삭제한다.")
  void deleteRefreshToken_success() {
    String token = "refresh.token";
    service.deleteRefreshToken(token);
    verify(jwtRegistry).invalidateByToken(token);
  }
}
