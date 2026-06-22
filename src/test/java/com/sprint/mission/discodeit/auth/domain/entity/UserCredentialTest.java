package com.sprint.mission.discodeit.auth.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.sprint.mission.discodeit.user.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserCredentialTest {

  @Test
  @DisplayName("updatePassword - 새로운 비밀번호가 이전 비밀번호와 다르면 업데이트되고 true를 반환한다.")
  void updatePassword_success() {
    User user = mock(User.class);
    UserCredential credential = UserCredential.builder()
        .user(user)
        .password("oldPassword")
        .build();

    credential.updatePassword("newPassword");

    assertThat(credential.getPassword()).isEqualTo("newPassword");
  }

  @Test
  @DisplayName("updatePassword - 새로운 비밀번호가 이전 비밀번호와 같으면 업데이트되지 않고 false를 반환한다.")
  void updatePassword_samePassword() {
    User user = mock(User.class);
    UserCredential credential = UserCredential.builder()
        .user(user)
        .password("oldPassword")
        .build();

    credential.updatePassword("oldPassword");

    assertThat(credential.getPassword()).isEqualTo("oldPassword");
  }
}
