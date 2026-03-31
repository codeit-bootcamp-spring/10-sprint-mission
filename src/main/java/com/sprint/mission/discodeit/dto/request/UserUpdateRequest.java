package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserUpdateRequest(
        @NotNull
        String newUsername,
        @NotNull
        @Email
        String newEmail,
        @NotBlank
        String newPassword
) {

}
