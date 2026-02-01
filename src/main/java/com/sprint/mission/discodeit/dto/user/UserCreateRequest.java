package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserCreateRequest(
        @NotBlank(message = "email이 입력되지 않았습니다.")
        @Pattern(regexp = "^\\S+$", message = "email은 공백이 허용되지 않습니다.")
        @Email(message = "email 형식에 맞지 않습니다.")
        String email,

        @NotBlank(message = "userName이 입력되지 않았습니다.")
        @Pattern(regexp = "^\\S+$", message = "userName는 공백이 허용되지 않습니다.")
        String userName,

        @NotBlank(message = "nickName이 입력되지 않았습니다.")
        @Pattern(regexp = "^\\S+$", message = "nickName은 공백이 허용되지 않습니다.")
        String nickName,

        @NotBlank(message = "password가 입력되지 않았습니다.")
        @Pattern(regexp = "^\\S+$", message = "password는 공백이 허용되지 않습니다.")
        String password,

        @NotBlank(message = "birthday가 입력되지 않았습니다.")
        @Pattern(regexp = "^\\d{8}$", message = "birthday는 YYYYMMDD 형식이어야 합니다.")
        String birthday,
        BinaryContentCreateRequest profileImage
) {
}
