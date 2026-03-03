package com.sprint.mission.discodeit.dto.user;

import lombok.Builder;
import lombok.NonNull;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public interface UserServiceDTO {
    record UserUniquenessDto(String username, String email) {
    }

    @Builder
    record UserUpdateDto(String username, String email, String password, UUID profileId) {}

    record UserProfileImageDto(String fileName, byte[] data) {}

    // todo: error log
    @Builder
    record UserResponse(@NonNull UUID id, @NonNull String username, @NonNull String email,
                        boolean online, UUID profileId, LocalDateTime createdAt, LocalDateTime updatedAt) {
    }
}
