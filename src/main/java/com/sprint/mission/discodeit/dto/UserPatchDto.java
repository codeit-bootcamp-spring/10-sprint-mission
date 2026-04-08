package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.Email;

public record UserPatchDto(
    String newUsername,

    @Email
    String newEmail,
    
    String newPassword
) {

}
