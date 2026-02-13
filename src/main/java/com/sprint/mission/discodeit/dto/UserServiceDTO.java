package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.dto.BinaryContentServiceDTO.BinaryContentCreateRequest;
import lombok.Builder;
import lombok.NonNull;

import java.util.UUID;

public interface UserServiceDTO {
    record UserCreateRequest(@NonNull String username, @NonNull String email, @NonNull String password,
                             BinaryContentCreateRequest profileImage) {
    }

    record UserUpdateRequest(@NonNull UUID userId, String newUsername, String newEmail, String newPassword,
                             UUID newProfileId) {
    }

    @Builder
    record UserResponse(@NonNull UUID userId, @NonNull String username, @NonNull String email,
                        boolean isActive, UUID profileId) {
    }
}
