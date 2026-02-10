package com.sprint.mission.discodeit.dto.binarycontent.request;

public record BinaryContentCreateRequest(
        String fileName,
        String contentType,
        byte[] content
) {}

