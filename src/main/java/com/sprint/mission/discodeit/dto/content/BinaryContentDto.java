package com.sprint.mission.discodeit.dto.content;

public record BinaryContentDto (
        String fileName,
        byte[] data
) {
}
