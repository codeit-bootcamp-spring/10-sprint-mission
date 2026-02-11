package com.sprint.mission.discodeit.dto.binarycontent;

public record BinaryContentRequest (
        String fileName,
        String contentType,
        byte[] bytes
) {}
