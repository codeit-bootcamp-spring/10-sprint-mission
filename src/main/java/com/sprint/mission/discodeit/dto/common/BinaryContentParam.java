package com.sprint.mission.discodeit.dto.common;

public record BinaryContentParam(
        byte[] data,
        String contentType
) {}