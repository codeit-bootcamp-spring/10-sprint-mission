package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record BinaryContentDto(
        UUID id,
        String fileName,
        long size,
        String contentType
) {
    public record BinaryContentCreateRequest(String contentType, String filename, byte[] bytes) { }
}
