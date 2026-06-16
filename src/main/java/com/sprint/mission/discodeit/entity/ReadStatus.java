package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdateEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(
        name = "read_statuses",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_read_statuses_user_channel",
                columnNames = {"user_id", "channel_id"}
        )
)
public class ReadStatus extends BaseUpdateEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Channel channel;

    @Column(name = "last_read_at", nullable = false)
    private Instant lastReadAt;

    @Column(name = "notification_enabled", nullable = false)
    private boolean notificationEnabled;


    public ReadStatus(User user, Channel channel, Instant lastReadAt) {
        this.user = user;
        this.channel = channel;
        //this.lastReadAt = Instant.EPOCH; // 1970년, 유저 생성 이전에 생성된 메시지 안읽음 처리
        this.lastReadAt = lastReadAt;
        this.notificationEnabled = channel.getType().equals(ChannelType.PRIVATE); // private면 true, public이면 false
    }

    public void update(Instant newLastReadAt, Boolean newNotificationEnabled) {
        if (newLastReadAt != null && !newLastReadAt.equals(this.lastReadAt)) {
            this.lastReadAt = newLastReadAt;
        }

        if (newNotificationEnabled != null && !newNotificationEnabled.equals(this.notificationEnabled))
        this.notificationEnabled = newNotificationEnabled;
    }

    public boolean hasRead(Instant messageCreatedAt) {
        return !messageCreatedAt.isAfter(this.lastReadAt);
    }

}
