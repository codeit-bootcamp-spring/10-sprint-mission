package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "LoginRequest", description = "로그인 정보")
public record LoginRequestDto(
        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @NotBlank(message = "이름은 필수입니다!")
        String username,

        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @NotBlank(message = "비밀번호는 필수입니다!")
        String password
) {}