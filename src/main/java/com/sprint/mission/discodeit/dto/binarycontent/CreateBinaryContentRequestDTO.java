package com.sprint.mission.discodeit.dto.binarycontent;

import jakarta.annotation.Nullable;

import java.util.UUID;

public record CreateBinaryContentRequestDTO(
        UUID userId,
        @Nullable
        UUID messageId,
        byte[] data,
        String contentType,
        String filename
) { }
