package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus extends MutableEntity {
    private static final long ONLINE_TIME_OUT_MS = 5 * 60_000;  // 5분
    private final UUID userId;
    private Instant lastActiveAt;

    public UserStatus(UUID userId) {
        super();
        this.userId = userId;
        this.lastActiveAt = Instant.now();
    }

    public void updateLastActiveAt() {
        this.lastActiveAt = Instant.now();
    }

    public boolean isOnline() {
        return Duration.between(this.lastActiveAt, Instant.now()).toMillis() <= ONLINE_TIME_OUT_MS;
    }
}
