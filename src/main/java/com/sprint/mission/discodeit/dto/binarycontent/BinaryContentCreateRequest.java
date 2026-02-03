package com.sprint.mission.discodeit.dto.binarycontent;

public record BinaryContentCreateRequest(
        String fileName,
        byte[] data
) {
}
