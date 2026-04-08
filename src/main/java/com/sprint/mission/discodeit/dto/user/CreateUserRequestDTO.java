package com.sprint.mission.discodeit.dto.user;

import jakarta.validation.constraints.*;

public record CreateUserRequestDTO(

        @NotBlank(message = "email은 공백일 수 없습니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @Size(max = 100, message = "email은 100자 이하여야 합니다.")
        String email,

        @NotBlank(message = "username은 공백일 수 없습니다.")
        @Size(min = 2, max = 20, message = "username은 2~20자여야 합니다.")
        String username,

        @NotBlank(message = "password는 공백일 수 없습니다.")
        @Size(min = 8, max = 20, message = "password는 8~20자여야 합니다.")
        String password
) {}