package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * 사용자가 채널 별 마지막으로 메세지를 읽은 시간을 표현하는 도메인 모델로,
 * 사용자별 각 채널에 읽지 않은 메시지를 확인하기 위해 활용
 */
@Getter
public class ReadStatus extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final UUID userId;
    private final UUID channelId;
    private Instant lastReadTime;

    // 생성자
    // 채널 생성/참여 시 함께 생성
    public ReadStatus(UUID userId, UUID channelId) {
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadTime = getCreatedAt();
    }

    // update - 메세지 확인 시, 시간 업데이트
    public void updateLastReadTime() {
        this.lastReadTime = Instant.now();
        updateTime();
    }
}
