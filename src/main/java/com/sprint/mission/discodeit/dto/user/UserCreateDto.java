package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserCreateDto(
        String username,
        @NotNull(message = "이메일 주소를 입력해주세요.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @NotEmpty(message = "이메일 주소를 입력해주세요.")
        String email,
        @NotNull
        String password,
        BinaryContentCreateDto profileDto
) {
}
