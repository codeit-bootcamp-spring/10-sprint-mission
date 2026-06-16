package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public class ReadStatusDto {
    @Schema(name = "ReadStatusCreateRequest", description = "Message 읽음 상태 생성 정보")
    public record CreateRequest(
            @NotNull(message = "유저 ID는 필수입니다.")
            UUID userId,
            @NotNull(message = "채널 ID는 필수입니다.")
            UUID channelId,
            Instant lastReadAt
    ) {}

    @Schema(name = "ReadStatusResponse", description = "Message 읽음 상태 상세 정보")
    public record Response(
            UUID id,
            UUID userId,
            UUID channelId,
            Instant lastReadAt,
            boolean notificationEnabled
    ) {}

    @Schema(name = "ReadStatusUpdateRequest", description = "수정할 읽음 상태 정보")
    public record UpdateRequest(
            Instant newLastReadAt,
            Boolean newNotificationEnabled
    ) {}
}
