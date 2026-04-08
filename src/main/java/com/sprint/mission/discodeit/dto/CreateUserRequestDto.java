package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "UserCreateRequest", description = "User 생성 정보")
public record CreateUserRequestDto(
        // Swagger 호환성을 위한 필드값 NOT_REQUIRED 처리
        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @NotBlank(message = "유저 이름은 필수입니다.")
        String username,

        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식을 맞춰서 작성해주세요.")
        String email,

        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
        String password,

        // 파일은 Multipart 파라미터로 받으므로, JSON 스키마에서는 숨김
        @Schema(hidden = true)
        BinaryContentDto profileImage
) {
}
