package com.sprint.mission.discodeit.user.entity;

import com.sprint.mission.discodeit.common.basicentity.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.Instant;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_statuses")
@Getter
@NoArgsConstructor
public class UserStatus extends BaseUpdatableEntity {

  @OneToOne
  @JoinColumn(name = "user_id", unique = true, nullable = false)
  private User user;

  @Column(nullable = false)
  private Instant lastActiveAt;


  public UserStatus(User user) {
    this.user = user;
    this.lastActiveAt = Instant.now();
  }

  public void updateConnection(Instant newLastReadAt) {
    this.lastActiveAt = newLastReadAt;
    this.updatedAt = lastActiveAt;
  }

  public boolean isOnline() {
    return lastActiveAt != null &&
        (Instant.now().getEpochSecond() - lastActiveAt.getEpochSecond() <= 300);
  }

}
