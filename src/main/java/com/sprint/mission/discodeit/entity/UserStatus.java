package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class UserStatus extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID userId;
    private LocalDateTime lastSeen;

    public UserStatus(UUID userId, LocalDateTime lastSeen) {
        super();
        this.userId = userId;
        this.lastSeen = lastSeen;
    }

    public boolean isOnline() {
        return lastSeen.isAfter(LocalDateTime.now().minusMinutes(5));
    }
}
