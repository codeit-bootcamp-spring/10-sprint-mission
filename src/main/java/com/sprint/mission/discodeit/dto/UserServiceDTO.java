package com.sprint.mission.discodeit.dto;

import lombok.NonNull;

import java.util.UUID;

public interface UserServiceDTO {
    record UserCreation(@NonNull String username,@NonNull String email,@NonNull String password) {}
    record UserInfoUpdate(@NonNull UUID userId, String newUsername, String newEmail, String newPassword) {}
}
