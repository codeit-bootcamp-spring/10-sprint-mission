package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import lombok.Getter;

@Getter
public class UserStatus extends Base {

  private UUID userId;
  private Instant lastActiveAt;
  private boolean online;

  public UserStatus(UUID userId) {
    this.userId = userId;
    this.lastActiveAt = Instant.now();
    this.online = true;
  }

  public void updateLastAccessedTime(Instant lastActiveAt) {
    this.lastActiveAt = lastActiveAt;
  }

  public void updateOnline(boolean online) {
    this.online = online;
  }

  public boolean isLoggedIn() {
    return Instant.now().isBefore(lastActiveAt.plus(5, ChronoUnit.MINUTES));
  }

}
