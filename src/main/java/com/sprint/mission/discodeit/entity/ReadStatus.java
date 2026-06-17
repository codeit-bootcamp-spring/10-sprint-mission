package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "read_statuses",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_read_statuses_user_channel", columnNames = {"user_id", "channel_id"})
        }
)
@Getter
@NoArgsConstructor
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

    public ReadStatus(User user, Channel channel) {
        this.user = user;
        this.channel = channel;
        this.lastReadAt = Instant.now();
        // PRIVATE 채널은 알림 ON, PUBLIC 채널은 알림 OFF로 초기화
        this.notificationEnabled = (channel.getType() == ChannelType.PRIVATE);
    }

    // 클라이언트가 보낸 특정 시간으로 업데이트 하는 메서드
    public void updateLastReadAt(Instant newTime) {
        this.lastReadAt = newTime;
    }

    public void updateLastReadAt(){
        this.lastReadAt = Instant.now();
    }

    public void updateNotificationEnabled(boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }

}
