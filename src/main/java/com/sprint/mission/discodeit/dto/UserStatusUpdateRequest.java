package com.sprint.mission.discodeit.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class UserStatusUpdateRequest {
    private UUID id;
    private LocalDateTime lastSeen;

    public UserStatusUpdateRequest(UUID id, LocalDateTime lastSeen) {
        this.id = id;
        this.lastSeen = lastSeen;
    }
}
