package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;

import java.time.Instant;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "read_statuses",
    uniqueConstraints = @UniqueConstraint(name = "user_channel_id",
        columnNames = {"user_id", "channel_id"}))
public class ReadStatus extends BaseUpdatableEntity {

  @Column(nullable = false)
  private Instant lastReadAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "channel_id", nullable = false)
  private Channel channel;

  @Column(nullable = false)
  private boolean notificationEnabled;

  public static ReadStatus create(User user, Channel channel, Instant lastReadAt) {
    return new ReadStatus(user, channel, lastReadAt);
  }

  private ReadStatus(User user, Channel channel, Instant lastReadAt) {
    this.user = user;
    this.channel = channel;
    this.lastReadAt = lastReadAt;
    this.notificationEnabled = channel.getType() == ChannelType.PRIVATE;
  }

  public void update(Instant lastReadAt, Boolean notificationEnabled) {
    if (lastReadAt != null) {
      this.lastReadAt = lastReadAt;
    }
    if (notificationEnabled != null) {
      this.notificationEnabled = notificationEnabled;
    }
  }
}
