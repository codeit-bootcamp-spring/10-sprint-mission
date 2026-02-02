package com.sprint.mission.discodeit.dto;

public record BinaryContentDTO(
        String fileName,
        String fileType,
        byte[] bytes
) {
}
