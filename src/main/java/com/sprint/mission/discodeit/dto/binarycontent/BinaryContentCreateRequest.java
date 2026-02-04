package com.sprint.mission.discodeit.dto.binarycontent;

public record BinaryContentCreateRequest(
        byte[] bytes,
        String contentType
) {}

