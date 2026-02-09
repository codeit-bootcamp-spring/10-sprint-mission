package com.sprint.mission.discodeit.binarycontent.dto;

import java.time.Instant;
import java.util.UUID;

public record BinaryContentInfo(
        UUID contentId,
        Instant createdAt,
        byte[] content
) {}
