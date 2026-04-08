package com.sprint.mission.discodeit.fixture;

import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public final class UserStatusFixture {

  private UserStatusFixture() {
  }

  private static Instant getInstant() {
    return Instant.now().truncatedTo(ChronoUnit.MILLIS);
  }

  private static LocalDateTime getLocalDateTime() {
    return LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
  }

  public static UserStatus createOnline() {
    return UserStatus.builder().lastActiveAt(getInstant()).build();
  }

  public static UserStatusUpdateRequest createUpdate() {
    return new UserStatusUpdateRequest(getLocalDateTime());
  }

  public static UserStatus createOffline() {
    return UserStatus.builder()
        .lastActiveAt(getInstant().minus(Duration.ofDays(7)))
        .build();
  }
}
