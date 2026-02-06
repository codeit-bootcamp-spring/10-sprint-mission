package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.UUID;

public class UserDto {
    public record CreateRequest(
            @NotBlank
            String username,

            @Email @NotBlank
            String email,

            @NotBlank
            String password,

            // 선택적 프로필 이미지
            BinaryContentDto.CreateRequest profileImage
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
            String newPassword,
            // 선택적 프로필 이미지
            BinaryContentDto.CreateRequest newProfileImage
    ) {}
}
