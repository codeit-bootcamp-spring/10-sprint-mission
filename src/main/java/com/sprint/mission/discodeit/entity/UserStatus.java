package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
@ToString
public class UserStatus {
    private final long ONLINE_THRESHOLD = 300L;

    private final UUID id = UUID.randomUUID();
    private final UUID userId;
    private final long createdAt = Instant.now().getEpochSecond();
    private long updatedAt = createdAt;
    @Setter
    private long lastOnlineTime;
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
}
