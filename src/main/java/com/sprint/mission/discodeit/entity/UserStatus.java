package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private final UUID userId;
    private Instant lastSeenAt;

    public UserStatus(UUID userId,  Instant lastSeenAt) {
        super();
        this.userId = userId;
        this.lastSeenAt = lastSeenAt;
    }

    public void updateLastSeenAt(Instant lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
        touch();
    }

    public boolean isOnline() {
        return lastSeenAt.isAfter(Instant.now().minusSeconds(300));
    }
}
