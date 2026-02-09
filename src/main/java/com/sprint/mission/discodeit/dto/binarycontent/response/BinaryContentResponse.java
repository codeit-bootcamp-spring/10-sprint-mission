package com.sprint.mission.discodeit.dto.binarycontent.response;

import java.time.Instant;
import java.util.UUID;

public record BinaryContentResponse(
        UUID binaryContentId,
        Instant createAt,
        byte[] content
) {
}
