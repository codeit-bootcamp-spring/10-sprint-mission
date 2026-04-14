package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "user_statuses")
@Getter
@ToString(callSuper = true, exclude = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserStatus extends BaseUpdatableEntity {

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;

  @Column(nullable = false)
  private Instant lastActiveAt;

  public UserStatus(User user, Instant lastActiveAt) {
    super();
    this.user = user;
    this.lastActiveAt = (lastActiveAt != null) ? lastActiveAt : Instant.now();
  }

  public void assignToUser(User user) { // 연관관계 편의 메서드
    this.user = user;
  }

  public boolean isOnline() {
    return this.lastActiveAt != null &&
        this.lastActiveAt.isAfter(Instant.now().minus(5, ChronoUnit.MINUTES));
  }

  public void updateLastActiveAt(Instant lastActiveAt) {
    if (lastActiveAt != null) {
      this.lastActiveAt = lastActiveAt;
    }
  }
}
