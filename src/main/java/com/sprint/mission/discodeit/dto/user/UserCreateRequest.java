package com.sprint.mission.discodeit.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
    @NotBlank(message = "유저 이름을 입력해주세요.")
    @Size(min = 2, max = 20)
    String username,

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email
    String email,

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 5, max = 20)
    String password
) {

}
