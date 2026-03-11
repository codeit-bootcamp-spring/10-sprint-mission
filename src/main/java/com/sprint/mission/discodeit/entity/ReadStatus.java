package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

// 사용자가 채널 별 마지막으로 메시지를 읽은 시간을 표현하는 도메인
// 사용자별 각 채널에 읽지 않은 메시지를 확인하기 위해 활용
@Getter
@NoArgsConstructor
@Entity
@Table(name = "read_statuses", uniqueConstraints = {
        @UniqueConstraint(
                columnNames = {"user_id","channel_id"}
        )
})
public class ReadStatus extends BaseUpdatableEntity {
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;
    @Column(nullable = false)
    private Instant lastReadAt;

    // private 채널 생성할 때 채널에 참여하는 User의 정보를 받아 User별 ReadStatus 정보를 생성함
    public ReadStatus(User user, Channel channel,Instant lastReadAt) {
        this.user = user;
        this.channel = channel;
        this.lastReadAt = lastReadAt;
    }
    
    // lastReadAt을 업데이트 하기 위한 메소드
    public void updateLastReadAt(Instant lastReadAt) {
        if (lastReadAt != null) {
            this.lastReadAt = lastReadAt;
        }
    }
}
