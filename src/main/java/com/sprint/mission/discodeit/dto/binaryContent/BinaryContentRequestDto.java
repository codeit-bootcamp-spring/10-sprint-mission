package com.sprint.mission.discodeit.dto.binaryContent;

import java.util.UUID;

public record BinaryContentRequestDto(
        UUID userId,
        UUID messageId,
        byte[] data,
        String contentType,
        String fileName
) {}
