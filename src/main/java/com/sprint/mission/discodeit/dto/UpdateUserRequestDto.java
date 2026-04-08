package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Schema(name = "UserUpdateRequest", description = "수정할 User 정보")
public record UpdateUserRequestDto(
        String newUsername,
        @Email(message = "이메일 형식을 맞춰서 작성해주세요.")
        String newEmail,

        @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
        String newPassword,

        @Schema(hidden = true)
        BinaryContentDto newProfileImage
) {}