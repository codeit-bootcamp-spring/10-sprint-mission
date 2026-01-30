package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@ToString
public class UserStatus implements Serializable {
    private static final long serialVersionUID = 1L;
    private final long ONLINE_THRESHOLD = 300L;

    @Getter
    private final UUID id = UUID.randomUUID();
    private final long createdAt = Instant.now().getEpochSecond();
    private long updatedAt = createdAt;
    @Setter
    private long lastOnlineTime = createdAt;
    private UserPresence presence = UserPresence.ONLINE;

    public UserPresence getPresence() {
        updatePresence();
        return presence;
    }

    private void updatePresence() {
        if (isOnline()) {
            if (presence == UserPresence.OFFLINE) {
                presence = UserPresence.ONLINE;
                updatedAt = Instant.now().getEpochSecond();
            }
            return;
        }
        if (presence == UserPresence.ONLINE) {
            presence = UserPresence.OFFLINE;
            updatedAt = Instant.now().getEpochSecond();
        }
    }

    private boolean isOnline() {
        long currentTime = Instant.now().getEpochSecond();
        return (currentTime - lastOnlineTime) < ONLINE_THRESHOLD;
    }

    public boolean isSamePresence(UserPresence presence) {
        return this.presence == presence;
    }
}
