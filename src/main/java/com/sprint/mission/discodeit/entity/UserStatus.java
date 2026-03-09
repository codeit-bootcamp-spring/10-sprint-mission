package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Duration;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "USER_STATUSES")
public class UserStatus extends BaseUpdatableEntity {

  @OneToOne
  @JoinColumn(name = "USER_ID")
  private User user;

  @Column(nullable = false)
  private Instant lastActiveAt;

  public UserStatus(User user) {
    this.user = user;
    this.lastActiveAt = Instant.now();
  }

  public void setUser(User user) {
    this.user = user;
    if (user.getStatus() != this) {
      user.setStatus(this);
    }
  }

  public void updateOnline(Instant lastActiveAt) {
    if (lastActiveAt != null) {
      this.lastActiveAt = lastActiveAt;
    }
  }

  public boolean isOnline() {
    Duration between = Duration.between(lastActiveAt, Instant.now());
    return between.toMinutes() <= 5;
  }
}

