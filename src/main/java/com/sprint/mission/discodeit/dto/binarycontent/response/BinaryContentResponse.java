package com.sprint.mission.discodeit.dto.binarycontent.response;

import java.util.UUID;

public record BinaryContentResponse(
        UUID binaryContentID,
        byte[] content,
        String contentType
) {
}
