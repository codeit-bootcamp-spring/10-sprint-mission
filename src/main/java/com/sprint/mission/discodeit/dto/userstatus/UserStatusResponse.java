package com.sprint.mission.discodeit.dto.userstatus;

import com.sprint.mission.discodeit.entity.UserOnlineStatus;
import com.sprint.mission.discodeit.entity.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "User 상태 응답 정보")
public record UserStatusResponse(
        @Schema(description = "UserStatus ID", example = "ee13e6b4-7a40-4c33-b2f6-c8bb5cda0794")
        UUID id,
        @Schema(description = "생성 시각", example = "2026-02-20T11:00:00Z")
        Instant createdAt,
        @Schema(description = "수정 시각", example = "2026-02-20T11:05:00Z")
        Instant updatedAt,
        @Schema(description = "User ID", example = "d2d837b7-acb8-4e6c-87ad-0f5841aa96b9")
        UUID userId,
        @Schema(description = "마지막 활동 시각", example = "2026-02-20T11:25:00Z")
        Instant lastActiveAt,
        @Schema(description = "온라인 여부", example = "true")
        Boolean online
) {
    public static UserStatusResponse of(UserStatus userStatus, UserOnlineStatus status) {
        return new UserStatusResponse(
                userStatus.getId(),
                userStatus.getCreatedAt(),
                userStatus.getUpdatedAt(),
                userStatus.getUserId(),
                userStatus.getLastActiveAt(),
                status == UserOnlineStatus.ONLINE
        );
    }
}
