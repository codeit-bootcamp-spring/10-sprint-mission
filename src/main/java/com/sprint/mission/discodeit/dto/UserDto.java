package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * User DTO 모음
 * - record + Validation 어노테이션으로 간결하게 유지
 * - Response는 엔티티(User) + online 정보를 합쳐 만드는 팩토리 메소드 제공
 */
public final class UserDto {

    public record CreateRequest(
            @NotBlank
            String username,

            @Email @NotBlank
            String email,

            @NotBlank
            String password

            // 선택적 프로필 이미지: null이면 "프로필 이미지 없음"으로 처리
//            @Valid
//            BinaryContentDto.CreateRequest profileImage
    ) {}


    public record Response(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            String username,
            String email,
            boolean isOnline,   // UserStatus에서 계산된 온라인 상태
            UUID profileId      // 선택: 프로필 이미지 ID
    ) {}


    public record UpdateRequest(
            // 선택: null이면 해당 필드는 변경하지 않음(부분 업데이트)
            String newUsername,

            @Email
            String newEmail,

            String newPassword

//            // 선택: null이면 프로필 변경 없음, 값이 있으면 "대체" 수행
//            @Valid
//            BinaryContentDto.CreateRequest newProfileImage
    ) {}
}
