package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.UserPresence;
import lombok.NonNull;

import java.util.UUID;

public interface UserServiceDTO {
    record UserCreation(@NonNull String username, @NonNull String email, @NonNull String password, String profileImageUrl) {
        public UserCreation {
            if (username.equals(email)) {
                throw new IllegalArgumentException(
                        "username(%s) must be different from email(%s)".formatted(username, email));
            }
        }
    }
    record UserInfoUpdate(@NonNull UUID userId, String newUsername, String newEmail, String newPassword, String newUrl) {}
    record UserFinding(@NonNull UUID userId, @NonNull UserPresence presence) {}
}
