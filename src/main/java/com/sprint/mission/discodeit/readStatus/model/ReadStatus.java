package com.sprint.mission.discodeit.readStatus.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class ReadStatus {
    private final UUID id;
    private final UUID userId;
    private final UUID channelId;
    private LocalDateTime lastReadAt;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ReadStatus(UUID id, UUID userId, UUID channelId, LocalDateTime lastReadAt,
                      LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ReadStatus create(UUID userId, UUID channelId) {
        LocalDateTime now = LocalDateTime.now();
        return new ReadStatus(UUID.randomUUID(), userId, channelId, now, now, now);
    }

    public void updateLastReadAt(LocalDateTime lastReadAt) {
        this.lastReadAt = lastReadAt;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public UUID getChannelId() { return channelId; }
    public LocalDateTime getLastReadAt() { return lastReadAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
