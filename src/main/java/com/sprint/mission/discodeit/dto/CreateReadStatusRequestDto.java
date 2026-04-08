package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

@Schema(name = "ReadStatusCreateRequest", description = "Message 읽음 상태 생성 정보")
public record CreateReadStatusRequestDto(
        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @NotNull(message = "유저 ID는 필수입니다.")
        UUID userId,

        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @NotNull(message = "채널 ID는 필수입니다.")
        UUID channelId,

        // 선택적으로 마지막 읽은 시간을 받을 수 있도록
        Instant lastReadAt
) {
}
