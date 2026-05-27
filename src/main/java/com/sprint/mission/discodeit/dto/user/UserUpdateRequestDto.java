package com.sprint.mission.discodeit.dto.user;

import jakarta.validation.constraints.Email;

import java.util.UUID;

public record UserUpdateRequestDto(
        String newUsername,

        @Email
        String newEmail,

        String newPassword
) {
}
