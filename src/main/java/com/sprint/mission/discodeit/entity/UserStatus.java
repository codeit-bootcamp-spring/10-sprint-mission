package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;

@Getter
public class UserStatus extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID userId;
    private LocalDateTime lastSeen;

    public UserStatus(UUID userId, LocalDateTime lastSeen) {
        super();
        this.userId = userId;
        this.lastSeen = lastSeen;
    }

    public void updateLastSeen(LocalDateTime lastSeen) {
        this.lastSeen = lastSeen;
        updateTimestamps();
    }

    public boolean isOnline() {
        return lastSeen.isAfter(LocalDateTime.now().minusMinutes(5));
    }
}
