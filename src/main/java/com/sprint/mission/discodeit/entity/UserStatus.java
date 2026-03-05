package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;

@Getter
@Entity
@Table(name="user_statuses")
@NoArgsConstructor
public class UserStatus extends BaseUpdatableEntity {

//  private static final long serialVersionUID = 1L;
//  private UUID id;
//  private Instant createdAt;
//  private Instant updatedAt;
  @OneToOne(optional = false)
  @JoinColumn(name="user_id", nullable = false, unique = true) //FK user_id
  private User user;

  @Column(name="last_active_at", nullable = false)
  private Instant lastActiveAt;

  public UserStatus(User user, Instant lastActiveAt) {
    this.user = user;
    this.lastActiveAt = (lastActiveAt!=null)?lastActiveAt:Instant.now();
  }

  public void update(Instant lastActiveAt) {
    if (lastActiveAt != null && !lastActiveAt.equals(this.lastActiveAt)) {
      this.lastActiveAt = lastActiveAt;
    }
  }

  public Boolean isOnline() {
    Instant instantFiveMinutesAgo = Instant.now().minus(Duration.ofMinutes(5));
    return lastActiveAt.isAfter(instantFiveMinutesAgo);
  }
}
