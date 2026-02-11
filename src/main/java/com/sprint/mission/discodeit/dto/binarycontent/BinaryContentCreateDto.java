package com.sprint.mission.discodeit.dto.binarycontent;

public record BinaryContentCreateDto(
        String fileName,
        byte[] bytes
) {
}
