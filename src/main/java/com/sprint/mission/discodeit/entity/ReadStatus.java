package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "read_statuses",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_user_channel",
            columnNames = {"user_id", "channel_id"})
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReadStatus extends BaseUpdatableEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "channel_id", nullable = false)
  private Channel channel;

  @Column(name = "last_read_at", nullable = false)
  private Instant lastReadAt;

  @Column(name = "notification_enabled", nullable = false)
  private boolean notificationEnabled;

  public ReadStatus(User user, Channel channel, boolean notificationEnabled) {
    super();
    this.user = user;
    this.channel = channel;
    this.lastReadAt = Instant.now();
    this.notificationEnabled = notificationEnabled;
  }

  public void updateLastReadAt() {
    this.lastReadAt = Instant.now();
  }

  public void updateNotificationEnabled(boolean notificationEnabled) {
    this.notificationEnabled = notificationEnabled;
  }

  @Override
  public String toString() {
    return String.format("ReadStatus[User: %s, Channel: %s, LastRead: %s]",
        user != null ? user.getId() : "null",
        channel != null ? channel.getId() : "null",
        lastReadAt);
  }
}
