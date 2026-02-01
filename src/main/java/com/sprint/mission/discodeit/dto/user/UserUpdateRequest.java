package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record UserUpdateRequest(
        @NotNull(message = "ID가 null입니다.")
        UUID userId,

        @Pattern(regexp = "^\\S+$", message = "email은 공백이 허용되지 않습니다.")
        @Email(message = "email 형식에 맞지 않습니다.")
        String email,

        @Pattern(regexp = "^\\S+$", message = "password는 공백이 허용되지 않습니다.")
        String password,

        @Pattern(regexp = "^\\S+$", message = "userName은 공백이 허용되지 않습니다.")
        String userName,

        @Pattern(regexp = "^\\S+$", message = "nickName은 공백이 허용되지 않습니다.")
        String nickName,

        @Pattern(regexp = "^\\d{8}$", message = "birthday는 YYYYMMDD 형식이어야 합니다.")
        String birthday,
        BinaryContentCreateRequest profileImage
) {
}
