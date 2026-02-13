package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.*;

import java.time.Instant;
import java.util.UUID;

public class UserDto {
    public record CreateRequest(
            @NotBlank(message = "사용자명은 필수입니다.")
            @Pattern(regexp = "^\\S+$", message = "사용자 이름에 공백을 포함할 수 없습니다.")
            String username,

            @Email @NotBlank(message = "이메일은 필수입니다.")
            String email,

            @NotBlank(message = "비밀번호는 필수입니다.")
            @Size(min = 4, message = "비밀번호는 최소 4자 이상이어야 합니다.")
            @Pattern(regexp = "^\\S+$", message = "비밀번호에 공백을 포함할 수 없습니다.")
            String password,

            UUID profileId
    ) {}

    public record Response(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            String username,
            String email,
            UUID profileId,   // 이미지 ID
            boolean Online       // UserStatus에서 계산된 값
    ) {}

    public record UpdateRequest(
            @Pattern(regexp = "^\\S*$", message = "새 사용자 이름에 공백을 포함할 수 없습니다.")
            String newUsername,

            @Email(message = "올바른 이메일 형식이어야 합니다")
            String newEmail,

            @Size(min = 4, message = "비밀번호는 최소 4자 이상이어야 합니다.")
            @Pattern(regexp = "^\\S*$", message = "새 비밀번호에 공백을 포함할 수 없습니다.")
            String newPassword,

            UUID profileId,

            // 요구 사항 외 구현, 유저 수정 시 프로필 삭제 여부
            Boolean isProfileDeleted
    ) {}
}
