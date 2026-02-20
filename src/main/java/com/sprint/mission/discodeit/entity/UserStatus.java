package com.sprint.mission.discodeit.entity;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public class UserStatus extends BaseEntity {

  private final UUID userId;
  private Instant lastActiveAt;

  public UserStatus(UUID userId) {
    this.userId = userId;
    this.lastActiveAt = Instant.now();
  }

  public void updateOnline(Instant lastActiveAt) {
    boolean anyValueUpdated = false;
    if (lastActiveAt != null && !lastActiveAt.equals(this.lastActiveAt)) {
      this.lastActiveAt = lastActiveAt;
      anyValueUpdated = true;
    }

    if (anyValueUpdated) {
      setUpdatedAt();
    }
  }

  public boolean isOnline() {
    Duration between = Duration.between(lastActiveAt, Instant.now());
    return between.toMinutes() <= 5;
  }
}
