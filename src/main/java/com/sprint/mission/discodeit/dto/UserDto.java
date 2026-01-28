package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.UUID;

public class UserDto {
    // =================================================================
    // 1. 회원가입 요청 (Create)
    // =================================================================
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

    // =================================================================
    // 2. 응답 (Response)
    // =================================================================
    public record Response(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            String username,
            String email,
            boolean isOnline,       // UserStatus에서 계산된 값
            UUID profileId   // 이미지 ID
    ) {
        // factory method, user와 userStatus에서 구한 online 정보를 합쳐서 DTO로 만듬
        public static Response from(User user, boolean isOnline) {
            return new Response(
                    user.getId(),
                    user.getCreatedAt(),
                    user.getUpdatedAt(),
                    user.getUsername(),
                    user.getEmail(),
                    isOnline,
                    user.getProfileId()
            );
        }
    }

    // =================================================================
    // 3. 수정 요청 (Update)
    // =================================================================
    public record UpdateRequest(
            @NotBlank
            String newUsername,
            @Email @NotBlank
            String newEmail,
            @NotBlank
            String newPassword,
            // 선택적 프로필 이미지
            BinaryContentDto.CreateRequest newProfileImage
    ) {}
}
