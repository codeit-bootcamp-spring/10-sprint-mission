package com.sprint.mission.discodeit.dto.binarycontent;

import java.time.Instant;
import java.util.UUID;

public record BinaryContentResponse(
    UUID id,
    String fileName,
    long size,
    String contentType,
    byte[] bytes,
    Instant createdAt
) {

}
