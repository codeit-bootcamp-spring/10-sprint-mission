package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus extends MutableEntity {
    private final UUID userId;
    private final UUID channelId;
    private Instant lastReadAt;

    public ReadStatus(UUID userId, UUID channelId) {
        super();
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = Instant.now();
    }

    public void updateLastReadAt(Instant lastReadAt) {
        this.lastReadAt = lastReadAt;
    }
}
