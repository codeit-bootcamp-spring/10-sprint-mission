package com.sprint.mission.discodeit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
    @Size(min = 2, max = 20, message = "유저 이름은 2자에서 20자 사이여야합니다.")
    String newUsername,
    @Email(message = "이메일에 유효한 형식이어야 합니다.")
    String newEmail,
    @Size(min = 10, message = "비밀번호는 10자 이상이어야 합니다.")
    String newPassword
) {

}
