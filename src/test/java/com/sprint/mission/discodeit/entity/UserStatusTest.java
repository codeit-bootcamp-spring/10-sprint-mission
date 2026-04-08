package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.fixture.UserStatusFixture;
import java.time.Instant;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserStatusTest {

  @Test
  @DisplayName("마지막 활성화 시간이 현재라면 isOnline() = true")
  void must_be_online() {
    UserStatus status = UserStatusFixture.createOnline();
    Assertions.assertThat(status.isOnline(Instant.now())).isTrue();
  }

  @Test
  @DisplayName("마지막 활성화 시간이 일주일 전이라면 isOnline() = false")
  void must_be_offline() {
    UserStatus status = UserStatusFixture.createOffline();
    Assertions.assertThat(status.isOnline(Instant.now())).isFalse();
  }
}
