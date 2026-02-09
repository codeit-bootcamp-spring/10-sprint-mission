package com.sprint.mission.discodeit.dto.binarycontent;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.UUID;

public record BinaryContentResponse(
        UUID id,
        UUID ownerId,
        byte[] image
) {
    public static BinaryContentResponse from(
            BinaryContent binaryContent
    ) {
        return new BinaryContentResponse(
                binaryContent.getId(),
                binaryContent.getOwnerId(),
                binaryContent.getImage()
        );
    }
}
