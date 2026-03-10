package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.Instant;


@Getter
@Entity
@Table(name="read_statuses")
@NoArgsConstructor
public class ReadStatus extends BaseUpdatableEntity {

  // User 단방향 참조
  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
  // Channel 단방향 참조
  @ManyToOne(optional = false)
  @JoinColumn(name = "channel_id", nullable = false)
  private Channel channel;

  @Column(name = "last_read_at", nullable = false)
  private Instant lastReadAt;

  public ReadStatus(User user, Channel channel, Instant lastReadAt) {
    this.user = user;
    this.channel = channel;
    // 없으면 현재값 반환
    this.lastReadAt = (lastReadAt != null) ? lastReadAt : Instant.now();
  }

  public void update(Instant lastReadAt) {
    if (lastReadAt != null && !lastReadAt.equals(this.lastReadAt)) {
      this.lastReadAt = lastReadAt;
    }
  }
}
