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
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;

@Entity
@Table(name = "read_statuses", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "channel_id"})}) // 복합 유니크 제약조건
@Getter
@ToString(callSuper = true, exclude = {"user", "channel"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReadStatus extends BaseUpdatableEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "channel_id", nullable = false)
  private Channel channel;

  @Column(nullable = false)
  private Instant lastReadAt;

  // 채널 알림 활성화 여부 필드
  @Column(nullable = false)
  private boolean notificationEnabled;

  public ReadStatus(User user, Channel channel, Instant lastReadAt) {
    super();
    this.user = user;
    this.channel = channel;
    this.lastReadAt = (lastReadAt != null) ? lastReadAt : Instant.now();
    // 기본적으로 PRIVATE 채널은 알림 켜짐, PUBLIC 채널은 알림 꺼짐
    this.notificationEnabled = channel.getType() == ChannelType.PRIVATE;
  }

  // 마지막으로 메시지 읽은 시간 저장
  public void updateLastReadAt(Instant lastReadAt) {
    this.lastReadAt = lastReadAt;
  }

  // 알림 설정 변경 메서드
  public void updateNotificationEnabled(boolean enabled) {
    this.notificationEnabled = enabled;
  }
}
