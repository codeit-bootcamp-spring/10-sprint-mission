package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.fixture.UserStatusFixture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {

  @Test
  @DisplayName("마지막 활성화 시간이 지금이면 User.isOnline() = true")
  void must_be_online() {
    UserStatus status = UserStatusFixture.createOnline();
    User user = User.builder()
        .status(status)
        .build();
    Assertions.assertThat(user.isOnline()).isTrue();
  }

  @Test
  @DisplayName("마지막 활성화 시간이 일주일 전이면 User.isOnline() = false")
  void must_be_offline() {
    UserStatus status = UserStatusFixture.createOffline();
    User user = User.builder()
        .status(status)
        .build();
    Assertions.assertThat(user.isOnline()).isFalse();
  }
}
