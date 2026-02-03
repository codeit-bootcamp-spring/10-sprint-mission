package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus {
    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;

    private final UUID userId;
    private final UUID channelId;
    private Instant lastReadAt;

    public ReadStatus(UUID userId, UUID channelId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = Instant.EPOCH;
    }

    public void markReadAt(Instant lastReadAt) {
        if (lastReadAt == null) {
            throw new RuntimeException("요청이 올바르지 않습니다.");
        }
        this.lastReadAt = lastReadAt;
        update();
    }

    public void update() {
        // 업데이트 시간 갱신
        this.updatedAt = Instant.now();
    }
}
