package com.sprint.mission.discodeit.dto.binaryContent;

import java.util.UUID;

public record BinaryContentCreateRequest(
        byte[] bytes,
        UUID userId,
        UUID messageId
) {
}
