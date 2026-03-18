package com.sprint.mission.discodeit.dto.userstatus;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "유저 상태 DTO")
public record UserStatusDto(
        @Schema(description = "유저 상태 ID", example = "f1909db5-4f00-4eaa-b77f-0d4f4cfbc8e2")
        UUID id,
        @Schema(description = "유저 ID", example = "d2d837b7-acb8-4e6c-87ad-0f5841aa96b9")
        UUID userId,
        @Schema(description = "마지막 활동 시각", example = "2026-03-10T10:45:00Z")
        Instant lastActiveAt
) {
}
