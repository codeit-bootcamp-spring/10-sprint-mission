package com.sprint.mission.discodeit.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginRequest(
        @NotBlank(message = "username 입력되지 않았습니다.")
        @Pattern(regexp = "^\\S+$", message = "username 공백이 허용되지 않습니다.")
        String username,

        @NotBlank(message = "password가 입력되지 않았습니다.")
        @Pattern(regexp = "^\\S+$", message = "password는 공백이 허용되지 않습니다.")
        String password
) {
}
