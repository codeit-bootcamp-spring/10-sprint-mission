package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
    @NotBlank(message = "이름은 필수입니다.")
    @Size(max = 50)
    String username,

    @NotBlank(message = "이메일은 필수입니다.")
    @Email
    @Size(max = 100)
    String email,

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(max = 60)
    String password
) {

}
