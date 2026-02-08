package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus extends Common implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID userId;

    private Instant lastOnlineTime;

    public UserStatus(UUID userId) {
        super();
        this.userId = userId;
        this.lastOnlineTime = Instant.now();
    }

    public void update(Instant newOnlineTime) {
        if (newOnlineTime != null && !newOnlineTime.isAfter(this.lastOnlineTime)) {
            this.lastOnlineTime = newOnlineTime;
            this.updatedAt = Instant.now();
        }
    }

    public boolean isOnline() {
        final int MINUTES_TO_CHECK = 5;
        return lastOnlineTime.isBefore(Instant.now().minus(Duration.ofMinutes(MINUTES_TO_CHECK)));
    }
}
