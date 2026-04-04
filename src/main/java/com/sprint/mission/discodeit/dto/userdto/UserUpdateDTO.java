package com.sprint.mission.discodeit.dto.userdto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserUpdateDTO(
    @NotBlank
    String newUsername,
    @NotBlank
    String newEmail,
    String newPassword

) {

}
