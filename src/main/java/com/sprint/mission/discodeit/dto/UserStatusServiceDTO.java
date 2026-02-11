package com.sprint.mission.discodeit.dto;

import lombok.NonNull;

import java.util.UUID;

public interface UserStatusServiceDTO {
    record UserStatusCreation(@NonNull UUID userId, long lastActiveAt) {}
    record UserStatusUpdate(UUID id, UUID userId, long lastActiveAt) {
        public UserStatusUpdate {
            if ((id == null) && (userId == null)) {
                throw new IllegalArgumentException(
                        "both can't be null, at least one of the two must not be null");
            }
        }
    }
    record UserStatusResponse(UUID id, UUID userId, long lastActiveAt) {}
}
