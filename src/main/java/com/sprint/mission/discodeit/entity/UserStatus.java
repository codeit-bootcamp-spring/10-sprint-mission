package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Getter
public class UserStatus extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private final UUID userId;
    private Instant lastOnlineAt;

    public UserStatus(UUID userId, Instant lastOnlineAt) {
        super();
        this.userId = userId;
        this.lastOnlineAt = lastOnlineAt;
    }

    public void update(Instant lastOnlineAt) {
        this.lastOnlineAt = lastOnlineAt;
        super.update();
    }

    public boolean isOnline() {
        if (getUpdatedAt() == null) return false;
        Instant fiveMinutesAgo = Instant.now().minus(5, ChronoUnit.MINUTES);
        return getUpdatedAt().isAfter(fiveMinutesAgo);
    }
}
