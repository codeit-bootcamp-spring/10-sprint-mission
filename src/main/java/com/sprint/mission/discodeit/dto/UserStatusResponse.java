package com.sprint.mission.discodeit.dto;

import lombok.Getter;
import lombok.ToString;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@ToString
public class UserStatusResponse {
    private final UUID id;
    private final UUID userId;
    private final LocalDateTime lastSeen;
    private final boolean isOnline;

    public UserStatusResponse(UUID id, UUID userId, LocalDateTime lastSeen, boolean isOnline) {
        this.id = id;
        this.userId = userId;
        this.lastSeen = lastSeen;
        this.isOnline = isOnline;
    }
}
