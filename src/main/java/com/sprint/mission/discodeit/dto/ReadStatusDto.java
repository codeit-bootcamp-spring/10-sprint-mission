package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;
@Getter
public class ReadStatusDto {
    public record CreateRquest(
            @NotBlank
            UUID userId,
            @NotBlank
            UUID channelId,
            Instant lastReadAt
    ) {}

    public record UpdateRequest(
            Instant newReadAt
    ) {}
}
