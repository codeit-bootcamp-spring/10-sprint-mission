package com.sprint.mission.discodeit.dto.binarycontentdto;

import java.time.Instant;
import java.util.UUID;

public record BinaryContentResponseDTO (
        UUID id,
        String contentType,
        Instant createdAt
)
{
}
