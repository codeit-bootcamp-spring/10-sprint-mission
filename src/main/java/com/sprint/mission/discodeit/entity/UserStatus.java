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
    private Boolean onlineOverride;

    public UserStatus(UUID userId, Instant lastSeenAt) {
        super();
        this.userId = userId;
        this.lastSeenAt = lastSeenAt;
        this.onlineOverride = null;
    }

    public void updateLastSeenAt(Instant lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
        this.onlineOverride = null;
        touch();
    }

    public void updateOnline(boolean online) {
        if (online) {
            this.lastSeenAt = Instant.now();
            this.onlineOverride = null;
        } else {
            this.onlineOverride = false;
        }
        touch();
    }

    public boolean isOnline() {
        if (onlineOverride != null) {
            return onlineOverride;
        }
        return lastSeenAt.isAfter(Instant.now().minusSeconds(300));
    }
}
