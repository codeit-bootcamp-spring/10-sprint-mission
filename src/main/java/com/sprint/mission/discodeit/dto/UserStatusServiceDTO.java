package com.sprint.mission.discodeit.dto;

import lombok.NonNull;

import java.util.UUID;

public interface UserStatusServiceDTO {
    record UserStatusCreation(@NonNull UUID userId, long lastActiveAt) {}
    record UserStatusUpdate(UUID id, UUID userId, long lastActiveAt) {}
    record UserStatusResponse(UUID id, long lastActiveAt) {}
}
