package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

// 사용자가 채널 별로 가장 최근 메시지를 읽은 시간을 나타냄
@Entity
@Table(
        name = "read_statuses",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "channel_id"})
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReadStatus extends BaseUpdatableEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @Column(columnDefinition = "timestamp with time zone",nullable = false)
    Instant lastReadAt;

    public ReadStatus(User user, Channel channel) {
        this.user = user;
        this.channel = channel;
        this.lastReadAt = Instant.now();
    }

    public void updateLastReadTime() {
        lastReadAt = Instant.now();
        updateUpdatedAt();
    }
}
