package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.BinaryContentType;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record BinaryContentResponseDTO (
        UUID id,
        Instant createdAt,
        String fileName,
        byte[] binaryContent,
        BinaryContentType binaryContentType
) {

}