package com.sprint.mission.discodeit.dto.readstatus;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "읽음 상태 DTO")
public record ReadStatusDto(
        @Schema(description = "읽음 상태 ID", example = "0f9238f3-cd81-4f82-8611-3a22b16da66d")
        UUID id,
        @Schema(description = "유저 ID", example = "d2d837b7-acb8-4e6c-87ad-0f5841aa96b9")
        UUID userId,
        @Schema(description = "채널 ID", example = "6e4be7c3-e196-447e-8b95-558e365fc01d")
        UUID channelId,
        @Schema(description = "마지막 읽음 시각", example = "2026-03-10T10:30:00Z")
        Instant lastReadAt
) {
}
