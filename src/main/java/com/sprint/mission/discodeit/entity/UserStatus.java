package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Getter
public class UserStatus extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private final UUID userId;

    public UserStatus(UUID userId) {
        super();
        this.userId = userId;
    }

    public boolean isOnline() {
        if (getUpdatedAt() == null) return false;
        Instant fiveMinutesAgo = Instant.now().minus(5, ChronoUnit.MINUTES);
        return getUpdatedAt().isAfter(fiveMinutesAgo);
    }
}
