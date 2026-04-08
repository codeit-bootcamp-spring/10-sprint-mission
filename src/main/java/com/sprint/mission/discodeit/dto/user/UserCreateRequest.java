package com.sprint.mission.discodeit.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "User 생성 정보")
public record UserCreateRequest(
        @Schema(description = "User 이름", example = "dustle")
        @NotBlank(message = "username은 비어 있을 수 없습니다.")
        @Size(max = 50, message = "username은 50자 이하여야 합니다.")
        String username,
        @Schema(description = "User 이메일", example = "dustle@email.com")
        @NotBlank(message = "email은 비어 있을 수 없습니다.")
        @Email(message = "email 형식이 올바르지 않습니다.")
        @Size(max = 100, message = "email은 100자 이하여야 합니다.")
        String email,
        @Schema(description = "User 비밀번호", example = "1234")
        @NotBlank(message = "password는 비어 있을 수 없습니다.")
        @Size(min = 4, max = 60, message = "password는 4자 이상 60자 이하여야 합니다.")
        String password
) {}
