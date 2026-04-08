package com.sprint.mission.discodeit.dto.readstatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "Message 읽음 상태 생성 정보")
public record ReadStatusCreateRequest(
        @Schema(description = "읽음 상태를 생성할 User ID", example = "d2d837b7-acb8-4e6c-87ad-0f5841aa96b9")
        @NotNull(message = "userId는 필수입니다.")
        UUID userId,
        @Schema(description = "읽음 상태를 생성할 Channel ID", example = "6e4be7c3-e196-447e-8b95-558e365fc01d")
        @NotNull(message = "channelId는 필수입니다.")
        UUID channelId,
        @Schema(description = "마지막으로 읽은 시각", example = "2026-02-20T12:00:00Z")
        @NotNull(message = "lastReadAt은 필수입니다.")
        Instant lastReadAt
) {
}
