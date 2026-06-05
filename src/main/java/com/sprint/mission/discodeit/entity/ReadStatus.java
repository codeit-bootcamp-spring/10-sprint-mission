package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "READ_STATUSES")
public class ReadStatus extends BaseUpdatableEntity {

  @ManyToOne
  @JoinColumn(name = "USER_ID")
  private User user;

  @ManyToOne
  @JoinColumn(name = "CHANNEL_ID")
  private Channel channel;

  @Column(nullable = false)
  private Instant lastReadAt;

  @Column(nullable = false)
  private Boolean notificationEnabled;

  public ReadStatus(User user, Channel channel, Instant lastReadAt, Boolean notificationEnabled) {
    this.user = user;
    this.channel = channel;
    this.lastReadAt = lastReadAt;
    this.notificationEnabled = notificationEnabled;
  }

  public void update(Instant newLastReadAt, Boolean newNotificationEnabled) {
    updateIfChanged(this.lastReadAt, newLastReadAt, val -> this.lastReadAt = val);
    updateIfChanged(this.notificationEnabled, newNotificationEnabled,
        val -> this.notificationEnabled = val);
  }
}

