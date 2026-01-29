package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus extends CommonEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final UUID userId;
    private final Instant lastSeenAt;

    public UserStatus(UUID userId, Instant lastSeenAt) {
        this.userId = userId;
        this.lastSeenAt = lastSeenAt;
    }

    public boolean isOnline() {
        return lastSeenAt != null &&
                lastSeenAt.isAfter(Instant.now().minusSeconds(5 * 60));
    }
}
