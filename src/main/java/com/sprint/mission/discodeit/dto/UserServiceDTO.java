package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.dto.BinaryContentServiceDTO.BinaryContentCreation;
import lombok.Builder;
import lombok.NonNull;

import java.util.UUID;

public interface UserServiceDTO {
    record UserCreation(@NonNull String username, @NonNull String email, @NonNull String password,
                        BinaryContentCreation profileImage, long lastActiveAt) {
    }

    record UsernamePassword(@NonNull String username, @NonNull String password) {
    }

    record UserInfoUpdate(@NonNull UUID userId, String newUsername, String newEmail, String newPassword,
                          UUID newProfileId) {
    }

    @Builder
    record UserResponse(UUID userId, String username, String email, boolean isActive, UUID profileId,
                        UUID userStatusId) {
    }
}
