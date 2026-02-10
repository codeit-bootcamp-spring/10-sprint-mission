package com.sprint.mission.discodeit.dto.binarycontent;

import java.util.UUID;

public record BinaryContentResponse(
        UUID binaryContentID,
        byte[] content,
        String contentType
) {
}
