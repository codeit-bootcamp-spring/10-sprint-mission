package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@JsonPropertyOrder({"id", "createdAt", "updatedAt", "userId", "lastActiveAt", "online"})
public class UserStatus implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int ONLINE_TIMEOUT_SECONDS = 300;

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
    private final UUID userId;
    private Instant lastActiveAt;

    public UserStatus(
            UUID userId,
            Instant lastActiveAt
    ) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.userId = userId;
        this.lastActiveAt = lastActiveAt;
    }

    public void markActive() {
        this.lastActiveAt = Instant.now();
        this.updatedAt = this.lastActiveAt;
    }

    public void updateLastActiveAt(Instant newLastActiveAt) {
        if (newLastActiveAt != null) {
            this.lastActiveAt = newLastActiveAt;
            this.updatedAt = Instant.now();
        }
    }

    public UserOnlineStatus getOnlineStatus() {
        Instant threshold = Instant.now()
                .minusSeconds(ONLINE_TIMEOUT_SECONDS);

        return lastActiveAt.isAfter(threshold)
                ? UserOnlineStatus.ONLINE
                : UserOnlineStatus.OFFLINE;
    }

    public boolean isOnline() {
        return getOnlineStatus() == UserOnlineStatus.ONLINE;
    }
}
