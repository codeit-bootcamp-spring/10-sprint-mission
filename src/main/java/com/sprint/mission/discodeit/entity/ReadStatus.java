package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

public class ReadStatus extends BaseEntity{
    @Getter
    private final UUID userId;
    @Getter
    private final UUID channelId;
    @Getter
    private Instant lastReadAt;

    public ReadStatus(UUID userId, UUID channelId) {
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = Instant.now();
    }

    public void updateLastReadAt() {
        this.lastReadAt = Instant.now();
        setUpdateAt();
    }
}

