package com.sprint.mission.discodeit.dto.binarycontent;

import java.time.Instant;
import java.util.UUID;

public record BinaryContentResponseDTO(
        UUID id,
        UUID userId,
        UUID messageId,
        long size,
        byte[] bytes,
        String contentType,
        String filename,
        Instant createdAt
) { }
