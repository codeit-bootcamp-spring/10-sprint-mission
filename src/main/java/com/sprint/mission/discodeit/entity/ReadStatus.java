package com.sprint.mission.discodeit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Entity
@Table(
        name = "read_statuses",
        // 제약 조건 추가
        uniqueConstraints = {
                @UniqueConstraint(
                        // 제약조건 이름
                        name = "unique_user_channel",
                        // 제약조건 테이블의 컬럼 이름
                        columnNames = { "user_id", "channel_id" }
                )
        }
)
@NoArgsConstructor
public class ReadStatus extends BaseUpdatableEntity{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @Column(name = "last_read_at", nullable = false)
    private Instant lastReadAt;

    @Column(name = "notification_enabled", nullable = false)
    private Boolean notificationEnabled;

    public ReadStatus(User user, Channel channel, Boolean notificationEnabled){
        this.user = user;
        this.channel = channel;
        this.lastReadAt = Instant.now();
        this.notificationEnabled = notificationEnabled;
    }

    public void updateLastReadTime(Instant newLastReadAt){
        this.lastReadAt = newLastReadAt;
    }

    public void updateNotificationEnabled(boolean enabled){
        this.notificationEnabled = enabled;
    }
}