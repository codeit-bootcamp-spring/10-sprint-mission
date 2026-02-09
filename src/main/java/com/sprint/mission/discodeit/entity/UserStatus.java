package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class UserStatus extends BaseEntity{
    @Getter
    private final UUID userId;
    @Getter
    private Instant lastActiveAt;

    public UserStatus(UUID userId) {
        this.userId = userId;
        this.lastActiveAt = Instant.now();
    }

    public void updateOnline() {
        this.lastActiveAt = Instant.now();
        setUpdatedAt();
    }

    public boolean isOnline() {
        Duration between = Duration.between(lastActiveAt, Instant.now());
        return between.toMinutes() <= 5;
    }
}
