package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus implements Serializable {
    private static final int ONLINE_TIMEOUT_SECONDS = 300;
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final UUID userId;
    private Instant lastActiveAt;

    public UserStatus(
            UUID userId,
            Instant lastActiveAt
    ) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.lastActiveAt = lastActiveAt;
    }

    public void markActive() {
        this.lastActiveAt = Instant.now();
    }

    public UserOnlineStatus getOnlineStatus() {
        Instant threshold = Instant.now()
                .minusSeconds(ONLINE_TIMEOUT_SECONDS);

        return lastActiveAt.isAfter(threshold)
                ? UserOnlineStatus.ONLINE
                : UserOnlineStatus.OFFLINE;
    }
}
