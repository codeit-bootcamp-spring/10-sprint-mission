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
    private Instant time;

    public ReadStatus(UUID userId, UUID channelId, Instant time) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
        this.userId = userId;
        this.channelId = channelId;
        this.time = time;
    }

    public void markAsRead(Instant readTime) {
        if (readTime == null) {
            return;
        }
        this.time = readTime;
        update();
    }

    public void markAsReadNow() {
        markAsRead(Instant.now());
    }

    public void update() {
        // 업데이트 시간 갱신
        this.updatedAt = Instant.now();
    }
}
