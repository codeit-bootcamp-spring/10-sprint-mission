package com.sprint.mission.discodeit.dto;

public record BinaryContentCreateDto(
        byte[] binaryContent
) {
    public BinaryContentCreateDto {
        if (binaryContent != null) {
            binaryContent = binaryContent.clone();
        }
    }
}
