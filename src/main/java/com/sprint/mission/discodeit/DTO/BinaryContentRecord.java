package com.sprint.mission.discodeit.DTO;

import java.util.UUID;

public record BinaryContentRecord(
        String contentType,
        byte[] bytes
) {}
