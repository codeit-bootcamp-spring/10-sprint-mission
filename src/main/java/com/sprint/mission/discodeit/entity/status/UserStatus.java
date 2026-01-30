package com.sprint.mission.discodeit.entity.status;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class UserStatus {
    private final UUID id;
    private final UUID userId;
    private Instant lastSeenAt;
    private final Instant createdAt;
    private Instant updatedAt;

    public UserStatus(UUID id, UUID userId, Instant lastSeenAt,
                      Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.lastSeenAt = lastSeenAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static UserStatus create(UUID userId) {
        Instant now = Instant.now();
        return new UserStatus(UUID.randomUUID(), userId, now, now, now);
    }

    public void updateLastSeenAt() {
        this.lastSeenAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    /**
     * 마지막 접속 시간이 현재로부터 5분 이내이면 온라인으로 판단
     */
    public boolean isOnline() {
        Instant fiveMinutesAgo = Instant.now().minus(Duration.ofMinutes(5));
        return lastSeenAt.isAfter(fiveMinutesAgo);
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public Instant getLastSeenAt() { return lastSeenAt; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
