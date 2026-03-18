package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
@Entity
@Table(name = "user_statuses")
public class UserStatus extends BaseUpdatableEntity {

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;

  @Column(name = "last_active_at", nullable = false)
  private Instant lastActiveAt;

  protected UserStatus() {
  }

  public UserStatus(User user, Instant lastActiveAt) {
    this.user = user;
    this.lastActiveAt = lastActiveAt;

    if (user != null) {
      user.bindStatus(this);   // cascade 연결
    }
  }

  public UUID getUserId() {
    return user == null ? null : user.getId();
  }

  public void updateLastSeenAt(Instant lastActiveAt) {
    this.lastActiveAt = lastActiveAt;
    touch();
  }

  public boolean isOnline() {
    return lastActiveAt != null &&
        lastActiveAt.isAfter(Instant.now().minusSeconds(300));
  }
}