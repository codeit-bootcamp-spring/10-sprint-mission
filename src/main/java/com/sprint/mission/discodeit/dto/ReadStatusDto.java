package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public class ReadStatusDto {
    public record CreateRequest(
            @NotNull(message = "유저 ID는 필수입니다.")
            UUID userId,
            @NotNull(message = "채널 ID는 필수입니다.")
            UUID channelId,
            Instant lastReadAt
    ) {}

    public record Response(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            UUID userId,
            UUID channelId,
            Instant lastReadAt
    ) {}

    public record UpdateRequest(
            Instant newLastReadAt
    ) {}
}
