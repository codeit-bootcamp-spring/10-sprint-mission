package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant updatedAt;
    private Instant createdAt;
    private UUID userId;
    private Instant lastActiveAt;

    public UserStatus(UUID userId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.userId = userId;
        this.lastActiveAt = Instant.now();
    }

    public void updateLastActiveAt(){
        this.lastActiveAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public boolean isOnline(){
        Instant now = Instant.now();
        Duration duration = Duration.between(lastActiveAt, now);
        return (duration.toMinutes() <= 5);
    }
}
