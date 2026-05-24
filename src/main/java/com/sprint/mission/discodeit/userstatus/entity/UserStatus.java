package com.sprint.mission.discodeit.userstatus.entity;

import com.sprint.mission.discodeit.common.entity.BaseUpdatableEntity;
import com.sprint.mission.discodeit.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Duration;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user_statuses")
public class UserStatus extends BaseUpdatableEntity {

  private static final Duration ONLINE_THRESHOLD_MINUTES = Duration.ofMinutes(5);

  @OneToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Column(nullable = false)
  private Instant lastActiveAt;

  @Builder
  public UserStatus(User user, Instant lastActiveAt) {
    this.user = user;
    this.lastActiveAt = lastActiveAt;
  }

  public boolean isOnline(Instant currentTime) {
    return this.lastActiveAt.isAfter(currentTime.minus(ONLINE_THRESHOLD_MINUTES));
  }

}
