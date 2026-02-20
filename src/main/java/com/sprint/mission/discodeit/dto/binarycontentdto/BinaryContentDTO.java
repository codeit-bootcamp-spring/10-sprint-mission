package com.sprint.mission.discodeit.dto.binarycontentdto;

import java.time.Instant;
import java.util.UUID;

public record BinaryContentDTO(
    UUID id,
    Instant createdAt,
    String fileName,
    int size,
    String contentType,
    byte[] bytes
) {

}
