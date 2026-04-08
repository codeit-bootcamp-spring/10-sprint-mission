package com.sprint.mission.discodeit.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @Size(min = 2, max = 20, message = "유저 이름은 2자 이상 20자 이하여야 합니다.")
        String newUsername,

        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String newEmail,

        @Size(min = 5, max = 20, message = "비밀번호는 5자 이상 20자 이하여야 합니다.")
        String newPassword
) {

}
