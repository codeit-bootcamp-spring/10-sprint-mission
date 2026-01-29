package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.UUID;

public class ReadStatusDto {
    public record CreateRequest(
            @NotBlank
            UUID userId,
            @NotBlank
            UUID channelId,
            Instant lastReadAt
    ) {}

    public record UpdateRequest(
            Instant newLastReadAt
    ) {}
}
