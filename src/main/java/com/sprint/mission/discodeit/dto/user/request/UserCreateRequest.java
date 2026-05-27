package com.sprint.mission.discodeit.dto.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "User 생성 정보")
public record UserCreateRequest(
        @NotBlank(message = "email이 입력되지 않았습니다.")
        @Pattern(regexp = "^\\S+$", message = "email은 공백이 허용되지 않습니다.")
        @Email(message = "newEmail 형식에 맞지 않습니다.")
        String email,

        @NotBlank(message = "newUsername 입력되지 않았습니다.")
        @Pattern(regexp = "^\\S+$", message = "newUsername 공백이 허용되지 않습니다.")
        String username,

        @NotBlank(message = "password가 입력되지 않았습니다.")
        @Pattern(regexp = "^\\S+$", message = "password는 공백이 허용되지 않습니다.")
        String password
) {
}
