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
        @UniqueConstraint(columnNames = {"user_id", "channel_id"})
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReadStatus extends BaseUpdatableEntity {

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", columnDefinition = "uuid")
  private User user;
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "channel_id", columnDefinition = "uuid")
  private Channel channel;
  @Column(columnDefinition = "timestamp with time zone", nullable = false)
  private Instant lastReadAt;

  /// 채널 알림 여부 속성
  @Column(nullable = false)
  private boolean notificationEnabled;

  public ReadStatus(User user, Channel channel, Instant lastReadAt) {
    this.user = user;
    this.channel = channel;
    this.lastReadAt = lastReadAt;

    /// PRIVATE 채널: 알림 true
    /// PUBLIC 채널: 알림 false
    this.notificationEnabled = channel.getType() == ChannelType.PRIVATE;

  }

  /// newNotificationEnabled를 boolean 타입으로 두면 null값이 false로 인식된다.
  /// 채널을 클릭할때 update의 경우 newNotificationEnabeld가 null값으로 들어오므로
  /// 채널을 옮겨다니는것만으로 채널 알림상태를 false로 만들고 다닌다.
  public void update(Instant newLastReadAt, Boolean newNotificationEnabled) {
    if (newLastReadAt != null && !newLastReadAt.equals(this.lastReadAt)) {
      this.lastReadAt = newLastReadAt;
    }
    if (newNotificationEnabled != null && this.notificationEnabled != newNotificationEnabled) {
      this.notificationEnabled = newNotificationEnabled;
    }
  }
}