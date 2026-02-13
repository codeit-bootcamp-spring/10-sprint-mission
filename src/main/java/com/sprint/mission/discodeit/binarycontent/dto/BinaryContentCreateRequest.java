package com.sprint.mission.discodeit.binarycontent.dto;

import java.util.UUID;

public record BinaryContentCreateRequest(
        byte[] bytes,
        UUID userId,
        UUID messageId
) {
}
