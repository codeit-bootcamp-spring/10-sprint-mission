package com.sprint.mission.discodeit.dto.user.request;

import com.sprint.mission.discodeit.dto.binarycontent.input.BinaryContentCreateInput;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public record UserUpdateRequest(
        @Pattern(regexp = "^\\S+$", message = "email은 공백이 허용되지 않습니다.")
        @Email(message = "email 형식에 맞지 않습니다.")
        String email,

        @Pattern(regexp = "^\\S+$", message = "password는 공백이 허용되지 않습니다.")
        String password,

        @Pattern(regexp = "^\\S+$", message = "username 공백이 허용되지 않습니다.")
        String username,

        @Pattern(regexp = "^\\S+$", message = "nickName은 공백이 허용되지 않습니다.")
        String nickName,

        @Pattern(regexp = "^\\d{8}$", message = "birthday는 YYYYMMDD 형식이어야 합니다.")
        String birthday,

        @Valid
        BinaryContentCreateInput profileImage
) {
}
