package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.UUID;

public class UserDto {
    public record CreateRequest(
            @NotBlank(message = "사용자명은 필수입니다.")
            String username,

            @Email @NotBlank(message = "이메일은 필수입니다.")
            String email,

            @NotBlank(message = "비밀번호는 필수입니다.")
            String password
    ) {}

    public record Response(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            String username,
            String email,
            boolean isOnline,       // UserStatus에서 계산된 값
            UUID profileId   // 이미지 ID
    ) {}

    public record UpdateRequest(
            String newUsername,
            @Email
            String newEmail,
            String newPassword
    ) {}
}
