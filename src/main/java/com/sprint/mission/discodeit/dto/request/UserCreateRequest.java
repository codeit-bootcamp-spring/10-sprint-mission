package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
        @NotBlank(message = "username은 필수입니다.")
        @Size(max=20, message="username은 20자 이하여야 합니다.")
        String username,
        @NotBlank(message = "email은 필수입니다.")
        @Email(message= "올바른 이메일 형식이어야 합니다.")
        String email,
        @NotBlank(message = "password는 필수입니다.")
        @Size(min=4, message="username은 4자 이상이어야 합니다.")
        String password
) {

}
