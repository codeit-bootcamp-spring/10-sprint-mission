package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.UUID;

public class BinaryContentDto {

    public record Create(
            String fileName,
            long size,
            String contentType,
            byte[] bytes
    ) {}

    public record Response(
            UUID binaryContentId,
            String fileName,
            long size,
            String contentType,
            byte[] bytes
    ) {
        public static Response of (BinaryContent content) {
            return new Response(
                    content.getId(),
                    content.getFileName(),
                    content.getSize(),
                    content.getContentType(),
                    content.getBytes()
            );
        }
    }

    //BinaryContent는 불변이기때문에 수정할 수 없음
}
