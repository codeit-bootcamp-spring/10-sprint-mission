package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Size;

public record UserUpdateRequest(

    @Size(max = 50)
    String newUsername,

    @Size(max = 100)
    String newEmail,

    @Size(max = 60)
    String newPassword
) {

}
