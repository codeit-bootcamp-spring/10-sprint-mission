package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus extends BaseEntity {

  private final UUID userId;
  private StatusType statusType;

  public UserStatus(UUID userId) {
    super(UUID.randomUUID(), Instant.now());
    this.userId = userId;
    this.statusType = StatusType.ONLINE;
  }

  public void updateStatusType(StatusType statusType) {
    this.statusType = statusType;
    this.onUpdate();
  }

  public boolean isOnline() {
    Instant now = Instant.now();
    Instant beforeFiveMinute = now.minus(Duration.ofMinutes(5));
    return updatedAt.isAfter(beforeFiveMinute);    // 마지막 접속 시간이 5분 전이면
  }

  public StatusType updateStatusType() {
    if (isOnline()) {
      this.statusType = StatusType.ONLINE;
    } else {
      this.statusType = StatusType.OFFLINE;
    }
    return statusType;
  }

  // 마지막 접속 시간 갱신
  public void updateLastActiveTime() {
    this.onUpdate();
  }

}
