package com.sprint.mission.discodeit.readStatus.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public class ReadStatus {
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

    // Getters
    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public UUID getChannelId() { return channelId; }
    public Instant getLastReadAt() { return lastReadAt; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
