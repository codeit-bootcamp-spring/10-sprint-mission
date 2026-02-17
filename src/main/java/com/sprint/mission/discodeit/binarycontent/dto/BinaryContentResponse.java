package com.sprint.mission.discodeit.binarycontent.dto;

import java.util.UUID;

public record BinaryContentResponse(
        byte[] bytes,
        UUID binaryContentId
) {
}
