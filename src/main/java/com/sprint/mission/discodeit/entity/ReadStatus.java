package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
@Getter
public class ReadStatus implements Serializable {
    private static final long serialVersionUID = 1L;
    /* 사용자가 채널 별 마지막으로 메시지를 읽은 시간을 표현하는 도메인 모델입니다.
    사용자별 각 채널에 읽지 않은 메시지를 확인하기 위해 활용합니다.
     */
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    //
    private UUID userId;
    private UUID channelId;

    public ReadStatus(UUID userId, UUID channelId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.userId = userId;
        this.channelId = channelId;
    }
    public void update() {
        this.updatedAt = Instant.now();
    }
}
