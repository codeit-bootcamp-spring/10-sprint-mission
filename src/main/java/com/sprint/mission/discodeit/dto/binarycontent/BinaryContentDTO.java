package com.sprint.mission.discodeit.dto.binarycontent;

public record BinaryContentDTO(
        String fileName,
        String fileType,
        byte[] bytes
) {
}
