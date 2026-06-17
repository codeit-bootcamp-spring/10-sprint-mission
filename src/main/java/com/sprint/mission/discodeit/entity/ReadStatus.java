package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * 사용자가 채널 별 마지막으로 메세지를 읽은 시간 및 알림 여부를 표현하는 도메인 모델로,
 * 사용자별 각 채널에 읽지 않은 메시지를 확인하기 위해 활용
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        name = "read_statuses",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_read_statuses_user_channel",
                        columnNames = {"user_id", "channel_id"}
                )
        }
)
public class ReadStatus extends BaseUpdatableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @Column(nullable = false)
    private Instant lastReadAt;

    @Column(nullable = false)
    private boolean notificationEnabled;

    // 생성자
    public ReadStatus(
            User user,
            Channel channel,
            Instant lastReadAt,
            boolean notificationEnabled
    ) {
        this.user = user;
        this.channel = channel;
        this.lastReadAt = lastReadAt;
        this.notificationEnabled = notificationEnabled;
    }

    // update - 메세지 확인 시, 시간 업데이트
    public void update(Instant lastReadAt, Boolean notificationEnabled) {
        if (lastReadAt != null) this.lastReadAt = lastReadAt;
        if (notificationEnabled != null) this.notificationEnabled = notificationEnabled;
    }
}
