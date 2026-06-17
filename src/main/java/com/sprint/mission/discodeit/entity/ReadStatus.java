package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(name = "read_statuses")
@Getter
public class ReadStatus extends BaseUpdatableEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "channel_id")
  private Channel channel;

  @Column(nullable = false)
  private Instant lastReadAt;

  @Column(nullable = false)
  private Boolean notificationEnabled;

  public ReadStatus(User user, Channel channel) {
    super();
    this.user = user;
    this.channel = channel;

    // Channel에 따른 초기화 설정(private: true, public: false)
    this.notificationEnabled = channel.getType() == ChannelType.PRIVATE;
  }

  @PrePersist
  void init() {
    this.lastReadAt = Instant.now();
  }

  public void updateLastReadAt(Instant lastReadAt) {
    this.lastReadAt = lastReadAt;
  }

  public void updateNotificationEnabled(Boolean notificationEnabled) {
    this.notificationEnabled = notificationEnabled;
  }
}
