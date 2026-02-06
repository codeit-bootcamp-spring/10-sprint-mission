package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.UUID;

/**
 * ReadStatus
 * 사용자가 채널 별 마지막으로 메시지를 읽은 시간을 표현하는 도메인 모델. 사용자별 각 채널에 읽지 않은 메시지를 확인하기 위해 활용한다.
 */

@Getter
public class ReadStatus extends BaseEntity {
    private final UUID userId;
    private final UUID channelId;

    public ReadStatus(UUID channelId, UUID userId) {
        super();
        this.channelId = channelId;
        this.userId = userId;
    }

    // 읽은 시간 갱신
    public void markAsRead() {
        updateInstant();
    }
}
