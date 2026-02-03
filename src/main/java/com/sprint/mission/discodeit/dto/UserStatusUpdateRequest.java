package com.sprint.mission.discodeit.dto;

import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class UserStatusUpdateRequest {
    private final UUID id;
    private final LocalDateTime lastSeen;

    public UserStatusUpdateRequest(UUID id, LocalDateTime lastSeen) {
        this.id = id;
        this.lastSeen = lastSeen;
    }
}
