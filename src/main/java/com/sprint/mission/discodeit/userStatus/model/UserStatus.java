package com.sprint.mission.discodeit.userStatus.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class UserStatus {
    private final UUID id;
    private final UUID userId;
    private LocalDateTime lastSeenAt;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserStatus(UUID id, UUID userId, LocalDateTime lastSeenAt,
                      LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.lastSeenAt = lastSeenAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static UserStatus create(UUID userId) {
        LocalDateTime now = LocalDateTime.now();
        return new UserStatus(UUID.randomUUID(), userId, now, now, now);
    }

    public void updateLastSeenAt() {
        this.lastSeenAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 마지막 접속 시간이 현재로부터 5분 이내이면 온라인으로 판단
     */
    public boolean isOnline() {
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minus(5, ChronoUnit.MINUTES);
        return lastSeenAt.isAfter(fiveMinutesAgo);
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public LocalDateTime getLastSeenAt() { return lastSeenAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
