package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @Size(max = 20, message = "username은 20자 이하여야 합니다.")
        String newUsername,
        @Email(message = "올바른 이메일 형식이어야 합니다.")
        String newEmail,
        @Size(min=4, max=20, message="password는 4자 이상이어야 합니다.")
        String newPassword
) {

}
