package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class MessageDto {
    public record CreateRequest(
            String content,
            @NotNull(message = "유저 ID는 필수입니다.")
            UUID authorId,
            @NotNull(message = "채널 ID는 필수입니다.")
            UUID channelId

    ) {}

    public record Response(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            String content,
            UUID channelId,
            UUID authorId,
            List<UUID> attachmentIds
    ) {}

    public record UpdateRequest(
            String content
    ) {}


}
