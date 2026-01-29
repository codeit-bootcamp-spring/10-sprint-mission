package com.sprint.mission.discodeit.dto.binaryContent;

public record BinaryContentRequestDto(
        byte[] data,
        String contentType,
        String fileName
) {}
