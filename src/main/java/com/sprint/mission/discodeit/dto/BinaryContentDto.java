package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.UUID;

public class BinaryContentDto {

    public record BinaryContentRequest(
            String filePath,
            String contentType
    ) {}

    public record BinaryContentResponse(
            UUID binaryContentId,
            String filePath,
            String contentType

    ){
        public static BinaryContentResponse from(BinaryContent binaryContent) {
            return new BinaryContentResponse(
                    binaryContent.getId(),
                    binaryContent.getFilePath(),
                    binaryContent.getContentType());
        }
    }


}
