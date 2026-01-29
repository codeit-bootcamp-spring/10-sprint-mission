package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    //
    private UUID userId;
    private Instant lastActiveAt;

    public UserStatus(UUID userId, Instant lastActiveAt) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.userId = userId;
    }

    public void update(Instant newLastActiveAt) {
        if (newLastActiveAt != null && !newLastActiveAt.equals(this.lastActiveAt)) {
            this.lastActiveAt = newLastActiveAt;
            this.updatedAt = Instant.now();
        }
    }

    public boolean isOnline() {
        final int DURATION_IN_MINUTES = 5;
        Instant fiveMinutesAgo = Instant.now().minus(Duration.ofMinutes(DURATION_IN_MINUTES));

         return lastActiveAt.isAfter(fiveMinutesAgo);
    }

}
