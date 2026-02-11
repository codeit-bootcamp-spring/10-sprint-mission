package com.sprint.mission.discodeit.dto.binarycontent;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.UUID;

public record BinaryContentResponse(
        UUID id,
        byte[] bytes,
        String contentType,
        String filename
) {
    public static BinaryContentResponse from(
            BinaryContent binaryContent
    ) {
        return new BinaryContentResponse(
                binaryContent.getId(),
                binaryContent.getBytes(),
                binaryContent.getContentType(),
                binaryContent.getFilename()
        );
    }
}
