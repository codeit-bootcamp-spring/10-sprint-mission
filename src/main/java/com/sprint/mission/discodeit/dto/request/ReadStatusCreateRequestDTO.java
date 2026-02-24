package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Message 읽음 상태 생성 정보")
public record ReadStatusCreateRequestDTO(
    UUID userId,
    UUID channelId,
    Instant lastReadAt
) {}
