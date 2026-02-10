package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Optional;

public record UserCreateRequest(
        @NotBlank(message = "사용자 이름을 입력해주세요.")
        @Size(min = 2, max = 20)
        String username,

        @NotBlank(message = "이메일을 입력해주세요.")
        @Email
        String email,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Size(min = 5, max = 20)
        String password,

        Optional<BinaryContentCreateRequest> profile
) {
}
