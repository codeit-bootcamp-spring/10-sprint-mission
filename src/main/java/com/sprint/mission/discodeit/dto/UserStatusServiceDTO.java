package com.sprint.mission.discodeit.dto;

import lombok.NonNull;

import java.util.UUID;

public interface UserStatusServiceDTO {
    record UserStatusCreateRequest(@NonNull UUID userId) {
    }

    record UserStatusUpdateRequest(UUID id, UUID userId) {
        public UserStatusUpdateRequest {
            if (id == null && userId == null) {
                throw new IllegalArgumentException(
                        "both can't be null, at least one of the two must not be null");
            }
        }
    }

    record UserStatusResponse(UUID id, UUID userId, boolean isActive) {
    }
}
