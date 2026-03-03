package com.sprint.mission.discodeit.dto.userstatus;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UserStatusServiceDTO {

    @Builder
    record UserStatusResponse(UUID id, UUID userId, boolean online, LocalDateTime createdAt, LocalDateTime updatedAt,
                              LocalDateTime lastActiveAt) {
    }
}
