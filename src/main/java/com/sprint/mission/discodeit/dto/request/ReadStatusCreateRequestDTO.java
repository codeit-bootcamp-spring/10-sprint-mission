package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusCreateRequestDTO(
    @NotBlank(message = "userId가 blank일 수 없습니다")
    UUID userId,
    @NotBlank(message = "channelId가 blank일 수 없습니다")
    UUID channelId,
    @NotBlank(message = "lastReadTime이 blank일 수 없습니다")
    Instant lastReadTime
) {}
