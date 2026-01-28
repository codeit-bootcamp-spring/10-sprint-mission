package com.sprint.mission.discodeit.dto;

public record BinaryContentDto (
        byte[] data,
        String contentType,
        String fileName
) {}
