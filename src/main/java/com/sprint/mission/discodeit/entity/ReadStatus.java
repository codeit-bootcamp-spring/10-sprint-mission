package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus extends BaseEntity {
    private UUID userId;                 // 사용자 고유 id (변경 불가능)
    private UUID channelId;              // 채널 고유 id (변경 불가능)
    private Instant lastReadTime;        // 해당 채널에서 마지막으로 메시지를 읽은 시간 (변경 가능)

    public ReadStatus(UUID userId, UUID channelId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.channelId = channelId;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.lastReadTime = Instant.now();
    }

    public void updateLastReadTime() {
        this.lastReadTime = Instant.now();
    }
}
