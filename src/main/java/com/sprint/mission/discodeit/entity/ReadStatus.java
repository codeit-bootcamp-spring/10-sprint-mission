package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
@Entity
@Table(name = "read_statuses")
public class ReadStatus extends BaseUpdatableEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "channel_id", nullable = false)
  private Channel channel;

  @Column(name = "last_read_at", nullable = false)
  private Instant readAt;

  @Column(name = "notification_enabled", nullable = false)
  private boolean notificationEnabled;

  protected ReadStatus() {
  }

  public ReadStatus(User user, Channel channel, boolean notificationEnabled) {
    this.user = user;
    this.channel = channel;
    this.readAt = Instant.now();
    this.notificationEnabled = notificationEnabled;
  }

  public UUID getUserId() {
    return user == null ? null : user.getId();
  }

  public UUID getChannelId() {
    return channel == null ? null : channel.getId();
  }

  public void updateLastReadAt(Instant time) {
    this.readAt = time;
    touch();
  }

  public void updateNotificationEnabled(boolean notificationEnabled) {
    this.notificationEnabled = notificationEnabled;
    touch();
  }
}