package com.sprint.mission.discodeit.dto.binarycontent;

import java.util.UUID;

public record BinaryContentCreateRequest(
        String fileName,
        String contentType,
        byte[] data,
        UUID profileUserId,
        UUID messageId
) {}
