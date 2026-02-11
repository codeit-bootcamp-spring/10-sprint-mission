package com.sprint.mission.discodeit.dto.binarycontent;

public record BinaryContentProfileRequest(
        String fileName,
        byte[] data
) {
}
