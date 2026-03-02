package com.sprint.mission.discodeit.dto.user;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserOnlineStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "User 응답 정보")
@JsonPropertyOrder({"id", "createdAt", "updatedAt", "username", "email", "profileId", "online"})
public record UserDto(
        @Schema(description = "User ID", example = "d2d837b7-acb8-4e6c-87ad-0f5841aa96b9")
        UUID id,
        @Schema(description = "생성 시각", example = "2026-02-20T11:00:00Z")
        Instant createdAt,
        @Schema(description = "수정 시각", example = "2026-02-20T11:05:00Z")
        Instant updatedAt,
        @Schema(description = "User 이름", example = "dustle")
        String username,
        @Schema(description = "User 이메일", example = "dustle@email.com")
        String email,
        @Schema(description = "프로필 이미지 ID", example = "a1636f5f-72ab-497f-a6ff-72e4eb6f9f54")
        UUID profileId,
        @Schema(description = "온라인 여부", example = "true")
        Boolean online
) {
    public static UserDto of(User user, UserOnlineStatus status) {
        return new UserDto(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileId(),
                status == UserOnlineStatus.ONLINE
        );
    }
}
