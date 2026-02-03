package com.sprint.mission.discodeit.dto.binaryContent;

import java.time.Instant;
import java.util.UUID;

public record BinaryContentResponseDto(
        Instant createdAt,
        UUID userId,
        byte[] data,
        String contentType,
        String fileName
) {}
