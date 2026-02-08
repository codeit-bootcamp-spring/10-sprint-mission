package com.sprint.mission.discodeit.entity.status;

import lombok.Getter;

import java.time.Instant;
import java.io.Serializable;
import java.util.UUID;

@Getter
public class ReadStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    // Getters
    private final UUID id;
    private final UUID userId;
    private final UUID channelId;
    private Instant lastReadAt;
    private final Instant createdAt;
    private Instant updatedAt;

    public ReadStatus(UUID id, UUID userId, UUID channelId, Instant lastReadAt,
                      Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ReadStatus create(UUID userId, UUID channelId) {
        Instant now = Instant.now();
        return new ReadStatus(UUID.randomUUID(), userId, channelId, now, now, now);
    }

    public void updateLastReadAt(Instant lastReadAt) {
        this.lastReadAt = lastReadAt;
        this.updatedAt = Instant.now();
    }

}
