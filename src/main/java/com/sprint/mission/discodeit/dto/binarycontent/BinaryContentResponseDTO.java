package com.sprint.mission.discodeit.dto.binarycontent;

import java.time.Instant;
import java.util.UUID;

public record BinaryContentResponseDTO(
        UUID id,
        Instant createdAt,
        String fileName,
        long size,
        String contentType,
        byte[] bytes
) { }
