package com.sprint.mission.discodeit.dto.binarycontent;

import java.util.UUID;

public record BinaryContentResponse(
        UUID id,
        String fileName,
        String contentType,
        long size,
        byte[] bytes
) {}

