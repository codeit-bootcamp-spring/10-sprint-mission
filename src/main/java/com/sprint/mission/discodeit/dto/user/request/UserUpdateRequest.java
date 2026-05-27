package com.sprint.mission.discodeit.dto.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "사용자 정보 수정 요청")
public record UserUpdateRequest(
        @NotNull(message = "사용자 이름은 빈 값이 아닙니다.")
        @Size(min = 2, max = 20, message = "이름은 2자 이상 20자 이하로 입력해주세요.")
        @Schema(description = "수정할 새로운 사용자 이름(닉네임)", example = "새로운 승주")
        String newUsername,

        @Schema(description = "수정할 새로운 이메일 주소", example = "new_seungju@example.com")
        @NotNull(message = "이메일은 빈 값이 아닙니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String newEmail,

        @NotNull(message = "비밀번호는 필수입니다.")
        @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
        @Schema(description = "수정할 새로운 비밀번호", example = "newpassword123!")
        String newPassword
) { }