package com.sprint.mission.discodeit.dto.userdto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserCreateRequestDTO(
    @NotBlank(message = "유저 이름은 공백이 될 수 없습니다!")
    String username,

    @NotBlank(message = "유저 이메일은 공백이 될 수 없습니다!")
    @Email
    String email,

    @NotBlank(message = "유저 비밀번호는 공백이 될 수 없습니다!")
    String password
) {

}
