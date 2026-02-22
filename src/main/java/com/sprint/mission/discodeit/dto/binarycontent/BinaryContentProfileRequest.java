package com.sprint.mission.discodeit.dto.binarycontent;

public record BinaryContentProfileRequest(
        String fileName,
        String contentType,
        byte[] bytes
) {
}
