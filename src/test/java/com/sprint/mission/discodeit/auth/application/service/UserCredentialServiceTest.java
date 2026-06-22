package com.sprint.mission.discodeit.auth.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.auth.domain.entity.UserCredential;
import com.sprint.mission.discodeit.auth.domain.exception.AuthException;
import com.sprint.mission.discodeit.auth.domain.exception.UserCredentialErrorCode;
import com.sprint.mission.discodeit.auth.domain.provider.AuthUserResolver;
import com.sprint.mission.discodeit.auth.infra.repository.UserCredentialJpaRepository;
import com.sprint.mission.discodeit.user.domain.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserCredentialServiceTest {

  @Mock
  private UserCredentialJpaRepository repository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private AuthUserResolver userProvider;

  @InjectMocks
  private UserCredentialService service;

  @Test
  @DisplayName("create - 유저 크레덴셜을 정상 생성한다.")
  void create_success() {
    UUID userId = UUID.randomUUID();
    User mockUser = mock(User.class);
    given(userProvider.getProxy(userId)).willReturn(mockUser);
    given(passwordEncoder.encode("password")).willReturn("encodedPassword");

    service.create(userId, "password");

    ArgumentCaptor<UserCredential> captor = ArgumentCaptor.forClass(UserCredential.class);
    verify(repository).save(captor.capture());
    UserCredential saved = captor.getValue();

    assertThat(saved.getUser()).isEqualTo(mockUser);
    assertThat(saved.getPassword()).isEqualTo("encodedPassword");
  }

  @Test
  @DisplayName("update - 유저 크레덴셜을 정상 업데이트한다.")
  void update_success() {
    UUID userId = UUID.randomUUID();
    UserCredential credential = mock(UserCredential.class);

    given(repository.findByUser_Id(userId)).willReturn(Optional.of(credential));
    given(passwordEncoder.encode("new-password")).willReturn("encodedNewPassword");

    service.update(userId, "new-password");

    verify(credential).updatePassword("encodedNewPassword");
  }

  @Test
  @DisplayName("update - 유저 크레덴셜이 존재하지 않으면 예외를 던진다.")
  void update_fail_not_found() {
    UUID userId = UUID.randomUUID();

    given(repository.findByUser_Id(userId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> service.update(userId, "new-password"))
        .isInstanceOf(AuthException.class)
        .hasFieldOrPropertyWithValue("errorCode",
            UserCredentialErrorCode.USER_CREDENTIAL_NOT_FOUND);
  }
}
